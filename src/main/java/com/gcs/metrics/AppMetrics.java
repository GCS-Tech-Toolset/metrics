/****************************************************************************
 * FILE: CtrailProps.java
 * DSCRPT: 
 ****************************************************************************/





package com.gcs.metrics;





import static com.gcs.metrics.cfg.MetricsUtils.buildMetricsKey;



import java.util.concurrent.atomic.AtomicLong;



import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;



import com.gcs.metrics.cfg.properties.MetricsConfigException;
import com.gcs.metrics.cfg.properties.MetricsProps;
import com.gcs.metrics.cfg.properties.MetricsPropsFactory;
import com.gcs.metrics.cfg.registries.InfluxMeterRegistryConfig;
import com.gcs.metrics.cfg.registries.RegistryNotReadyException;
import com.gcs.metrics.cfg.registries.StatsdMeterRegistryConfig;
import com.gcs.metrics.types.TsfCounter;



import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.JvmCompilationMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.influx.InfluxMeterRegistry;
import io.micrometer.statsd.StatsdMeterRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;





@Slf4j
public class AppMetrics
{
	@Getter @Setter private CompositeMeterRegistry	_registry;
	@Getter @Setter private MetricsProps			_metricsConfig;
	@Getter @Setter private boolean					_enabled;





	private AppMetrics()
	{
		try
		{
			_registry = new CompositeMeterRegistry(Clock.SYSTEM);
		}
		catch (Exception ex_)
		{
			_logger.error(ex_.toString(), ex_);
			throw new RegistryNotReadyException("AppMetrics::AppMetrics() - registy not ready!");
		}
	}





	public static AppMetrics initFromConfig(String cfg_) throws MetricsConfigException
	{
		try
		{
			_logger.info("loading config from:{}", cfg_);
			final Parameters params = new Parameters();
			final FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
					.configure(params.xml()
							.setThrowExceptionOnMissing(false)
							.setEncoding("UTF-8")
							.setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
							.setValidating(false)
							.setFileName(cfg_));
			final XMLConfiguration config = builder.getConfiguration();
			return initFromConfig(config);
		}
		catch (ConfigurationException ex_)
		{
			_logger.error(ex_.toString(), ex_);
		}

		return null;
	}





	public static AppMetrics initFromConfig(XMLConfiguration cfg_) throws MetricsConfigException
	{

		AppMetrics appM = getInstance();

		appM.setEnabled(cfg_.getBoolean("AppMetrics.Enabled", true));
		appM._registry.add(new SimpleMeterRegistry(SimpleConfig.DEFAULT, Clock.SYSTEM));
		new TimedAspect(appM._registry);
		if (!appM.isEnabled())
		{
			_logger.info("application metrics are not enabled, no further configuration will be read/applied");
			return appM;
		}

		appM.setMetricsConfig(appM.loadConfig(cfg_));
		appM.configureRegistry();
		return appM;
	}





	public Timer createTimer(@NonNull String name_)
	{
		return createTimer(name_, null);
	}





	public Timer createTimer(@NonNull String name_, String[] tags_)
	{
		if (_registry == null)
		{
			throw new RegistryNotReadyException("createTimer - registy not ready!");
		}

		if (_logger.isTraceEnabled())
		{
			_logger.trace("building timer:{}", name_);
		}


		return Timer
				.builder(name_)
				.tags(getCommonTags())
				.description("a description of what this timer does") // optional
				.publishPercentileHistogram()
				.register(_registry);
	}





	public AtomicLong createGauge(@NonNull String name_)
	{
		if (_registry == null)
		{
			throw new RegistryNotReadyException("createTimer - registy not ready!");
		}

		if (_logger.isTraceEnabled())
		{
			_logger.trace("building timer:{}", name_);
		}

		AtomicLong val = new AtomicLong(0);
		_registry.gauge(name_, getCommonTags(), val);
		return val;
	}





	public Counter createCounter(@NonNull String name_, String[] tags_)
	{
		if (_registry == null)
		{
			throw new RegistryNotReadyException("createTimer - registy not ready!");
		}

		if (_logger.isTraceEnabled())
		{
			_logger.trace("building timer:{}", name_);
		}

		return _registry.counter(name_, getCommonTags());
	}





	public Counter createCounter(@NonNull String name_)
	{
		return createCounter(name_, null);
	}





	public TsfCounter createTsfCounter(@NonNull String name_)
	{
		if (_logger.isTraceEnabled())
		{
			_logger.trace("building TsfCounter:{}", name_);
		}
		return new TsfCounter(name_, false);
	}





	public TsfCounter createTsfCounter(@NonNull String name_, boolean autoIncrement_)
	{
		if (_logger.isTraceEnabled())
		{
			_logger.trace("building TsfCounter:{}", name_);
		}
		return new TsfCounter(name_, autoIncrement_);
	}





	private MetricsProps loadConfig(XMLConfiguration cfg_) throws MetricsConfigException
	{
		_enabled = cfg_.getBoolean(buildMetricsKey("Enabled"), false);
		if (!_enabled)
		{
			_logger.error("Metrics not enabled, config load abandoned");
			return null;
		}
		else
		{
			if (_logger.isTraceEnabled())
			{
				_logger.trace("Metrics enabled, config loading");
			}
		}


		try
		{
			var key = buildMetricsKey("Registry");
			MetricsProps props = MetricsPropsFactory.getMetricsProps(cfg_.getString(key));
			props.loadFromXml(cfg_);

			if (_logger.isInfoEnabled())
			{
				_logger.info("app-name:{}", props.getAppName());
				_logger.info("host:{}", props.getHostname());
				_logger.info("env:{}", props.getEnv());
			}
			return props;
		}
		catch (ConfigurationException ex_)
		{
			throw new MetricsConfigException(ex_);
		}
	}





	private void configureRegistry() throws MetricsConfigException
	{
		if (!_enabled)
		{
			_logger.warn("influx metrics not enabled!");
			return;
		}

		MeterRegistry meterRegistry;

		final String registryType = _metricsConfig.getRegistry();
		if (StringUtils.equalsIgnoreCase("Influx", registryType))
		{
			InfluxMeterRegistryConfig meterRegistryConfig = new InfluxMeterRegistryConfig(_metricsConfig);
			meterRegistry = new InfluxMeterRegistry(meterRegistryConfig, Clock.SYSTEM);
		}
		else if (StringUtils.equalsIgnoreCase("Datadog", registryType))
		{
			StatsdMeterRegistryConfig meterRegistryConfig = new StatsdMeterRegistryConfig(_metricsConfig);
			meterRegistry = new StatsdMeterRegistry(meterRegistryConfig, Clock.SYSTEM);
		}
		else
		{
			_logger.error("unkown registry trype:{}", registryType);
			throw new MetricsConfigException("Unknown registry type");
		}

		_registry.add(meterRegistry);
		new JvmMemoryMetrics().bindTo(_registry);
		new JvmGcMetrics().bindTo(_registry);
		new JvmThreadMetrics().bindTo(_registry);
		new JvmCompilationMetrics().bindTo(_registry);
		new ProcessorMetrics().bindTo(_registry);
	}





	private Tags getCommonTags()
	{
		if (_metricsConfig == null)
		{
			return null;
		}
		else
		{
			return _metricsConfig.getCommonTags();
		}
	}





	///////////////////////////////
	//
	////////////////////////////////
	public static final AppMetrics getInstance()
	{
		return SingletonAppMetrics._instance;
	}





	private static final class SingletonAppMetrics
	{
		private static final AppMetrics _instance = new AppMetrics();
	}


}
