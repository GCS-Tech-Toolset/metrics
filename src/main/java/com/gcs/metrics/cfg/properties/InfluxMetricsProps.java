




package com.gcs.metrics.cfg.properties;





import static com.gcs.metrics.cfg.MetricsUtils.buildMetricsKey;
import static com.gcs.metrics.cfg.MetricsUtils.decryptValue;



import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.lang3.StringUtils;



import lombok.Data;
import lombok.ToString;





@Data
@ToString
public class InfluxMetricsProps extends MetricsProps
{
	private String	_dbName;
	private String	_dbUsername;
	private String	_dbPassword;
	private String	_uri;
	private boolean	_autoCreateDb;
	private int		_batchSize;
	private String	_numberOfInfluxThreads	= "2";
	private String	_influxReadTimeout		= "10s";
	private String	_influxConnectTimeout	= "1s";
	private String	_influxRetentionPolicy	= "Retention";
	private String	_influxCompressed		= "true";
	private String	_influxConsistency		= "one";





	@Override
	public void loadFromConfig(XMLConfiguration xmlConfiguration_) throws MetricsConfigException
	{
		//
		// basics
		//
		loadBasePropsFromConfig(xmlConfiguration_);



		//
		// this registry
		//
		_registry = "Influx";
		_dbName = xmlConfiguration_.getString(buildMetricsKey("Influx.Name"));
		_uri = xmlConfiguration_.getString(buildMetricsKey("Influx.URI"));
		_autoCreateDb = xmlConfiguration_.getBoolean(buildMetricsKey("Influx.AutoCreateDb"), true);
		_batchSize = xmlConfiguration_.getInt(buildMetricsKey("Influx.BatchSizing"), 1000);

		


		//
		// creds
		//
		boolean encryptedCreds = xmlConfiguration_.getBoolean(buildMetricsKey("EncryptedCredentials"), false);
		if (encryptedCreds)
		{
			_dbUsername = decryptValue(xmlConfiguration_, buildMetricsKey("Influx.UserName"), "grafana");
			_dbPassword = decryptValue(xmlConfiguration_, buildMetricsKey("Influx.Pasword"), "grafana");
		}
		else
		{
			_dbUsername = xmlConfiguration_.getString(buildMetricsKey("Influx.UserName"), "grafana");
			_dbPassword = xmlConfiguration_.getString(buildMetricsKey("Influx.Pasword"), "grafana");
		}





		//
		//
		//
		exceptOnEmpty("registry", _registry);
		exceptOnEmpty("Influx.Name", _dbName);
		exceptOnEmpty("Influx.URI", _uri);
		exceptOnEmpty("Influx.UserName", _dbUsername);
		exceptOnEmpty("Influx.Pasword", _dbPassword);




		//
		//
		//
		configureEnvironment();
	}





	private void exceptOnEmpty(String key_, String value_) throws MetricsConfigException
	{
		if (StringUtils.isEmpty(value_))
		{
			throw new MetricsConfigException("value not set, key:" + key_);
		}
	}





	private void configureEnvironment()
	{
		// # Whether to create the Influx database if it does not exist before attempting to publish metrics to it.
		System.setProperty("management.metrics.export.influx.auto-create-db", Boolean.toString(_autoCreateDb));

		// Number of measurements per request to use for this backend. If more measurements are found, then multiple requests will be made.
		System.setProperty("management.metrics.export.influx.batch-size", Integer.toString(_batchSize));

		// Whether exporting of metrics to this backend is enabled.
		System.setProperty("management.metrics.export.influx.enabled", Boolean.toString(_enabled));

		// Step size (i.e. reporting frequency) to use.
		System.setProperty("management.metrics.export.influx.step", _reportingFrequencyInSeconds + "s");

		// URI of the Influx server.
		System.setProperty("management.metrics.export.influx.uri", _uri);
		System.setProperty("management.metrics.export.influx.user-name", _dbUsername);
		System.setProperty("management.metrics.export.influx.password", _dbPassword);
		System.setProperty("management.metrics.export.influx.db", _dbName);

		// Number of threads to use with the metrics publishing scheduler.
		System.setProperty("management.metrics.export.influx.num-threads", _numberOfInfluxThreads);

		// Read timeout for requests to this backend.
		System.setProperty("management.metrics.export.influx.read-timeout", _influxReadTimeout);

		// policy to use (Influx writes to the DEFAULT retention policy if one is not specified).
		System.setProperty("management.metrics.export.influx.retention-policy", _influxRetentionPolicy);

		// Whether to enable GZIP compression of metrics batches published to Influx.
		System.setProperty("management.metrics.export.influx.compressed", _influxCompressed);

		// Connection timeout for requests to this backend.
		System.setProperty("management.metrics.export.influx.connect-timeout", _influxConnectTimeout);

		// Write consistency for each point.
		System.setProperty("management.metrics.export.influx.consistency", _influxConsistency);
	}
}
