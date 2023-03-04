




package com.gcs.metrics.cfg.properties;





import lombok.Data;
import lombok.NonNull;



import static com.gcs.metrics.cfg.MetricsUtils.buildMetricsKey;



import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;





@Data
public class StatsdMetricsProps extends MetricsProps
{
	private String	_hostIP;
	private int		_port;





	@Override
	public void loadFromXml(@NonNull XMLConfiguration cfg_) throws ConfigurationException
	{
		_registry = "Datadog";
		_hostIP = cfg_.getString(buildMetricsKey("Datadog.HostIP"), "localhost");
		_port = cfg_.getInt(buildMetricsKey("Datadog.Port"), 8125);
		loadBasePropsFromConfig(cfg_);
	}





}
