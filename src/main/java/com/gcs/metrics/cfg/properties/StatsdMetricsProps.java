




package com.gcs.metrics.cfg.properties;





import lombok.Data;



import static com.gcs.metrics.cfg.MetricsUtils.buildMetricsKey;



import org.apache.commons.configuration2.XMLConfiguration;





@Data
public class StatsdMetricsProps extends MetricsProps
{
	private String	_hostIP;
	private int		_port;





	@Override
	public void loadFromConfig(XMLConfiguration xmlConfiguration_)
	{
		_registry = "Datadog";
		_hostIP = xmlConfiguration_.getString(buildMetricsKey("Datadog.HostIP"), "localhost");
		_port = xmlConfiguration_.getInt(buildMetricsKey("Datadog.Port"), 8125);
		loadBasePropsFromConfig(xmlConfiguration_);
	}
}
