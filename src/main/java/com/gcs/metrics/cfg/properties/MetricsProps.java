




package com.gcs.metrics.cfg.properties;





import static com.gcs.metrics.cfg.MetricsUtils.buildMetricsKey;



import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;



import org.apache.commons.configuration2.XMLConfiguration;



import com.gcs.config.IProps;



import io.micrometer.core.instrument.Tags;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;





@Slf4j
@Data
public abstract class MetricsProps implements IProps
{
	protected boolean	_enabled;
	protected int		_reportingFrequencyInSeconds;
	protected String	_hostname;
	protected String	_env;
	protected String	_appName;
	protected String	_registry;
	protected Tags		_commonTags;





	public Map<String, String> toMap()
	{
		return null;
	}





	public void loadBasePropsFromConfig(XMLConfiguration xmlConfiguration_)
	{
		_reportingFrequencyInSeconds = xmlConfiguration_.getInt(buildMetricsKey("ReportingFrequencyInSeconds"), 5);
		_enabled = xmlConfiguration_.getBoolean(buildMetricsKey("Enabled"), true);
		_appName = xmlConfiguration_.getString(buildMetricsKey("AppName"), "Unknown");
		_env = xmlConfiguration_.getString(buildMetricsKey("Environment"), "localhost");

		try
		{
			_hostname = InetAddress.getLocalHost().getHostName();
			_commonTags = Tags.of("hostname", _hostname, "env", _env, "service", _appName);
		}
		catch (UnknownHostException ex_)
		{
			_logger.error(ex_.toString(), ex_);
		}

	}

}
