




package com.gcs.metrics.cfg.registries;





import io.micrometer.influx.InfluxConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;



import java.time.Duration;



import com.gcs.metrics.cfg.properties.InfluxMetricsProps;
import com.gcs.metrics.cfg.properties.MetricsProps;





@Builder
@AllArgsConstructor
public class InfluxMeterRegistryConfig implements InfluxConfig
{
	InfluxMetricsProps _influxMetricsProps;





	public InfluxMeterRegistryConfig(MetricsProps props_)
	{
		_influxMetricsProps = (InfluxMetricsProps) props_;
	}





	@Override
	public Duration step()
	{
		return Duration.ofSeconds(_influxMetricsProps.getReportingFrequencyInSeconds());
	}





	@Override
	public String db()
	{
		return _influxMetricsProps.getDbName();
	}





	@Override
	public String get(String key_)
	{
		return null;
	}





	@Override
	public String uri()
	{
		return _influxMetricsProps.getUri();
	}
}
