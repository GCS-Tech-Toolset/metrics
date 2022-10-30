/****************************************************************************
 * FILE: TSFCounterTest.java
 * DSCRPT: 
 ****************************************************************************/





package com.gcs.metrics.types;





import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;



import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Ignore;
import org.junit.Test;



import com.gcs.metrics.AppMetrics;





public class TsfCounterTest
{




	private void initMetricsRepo() throws ConfigurationException
	{
		final Parameters params = new Parameters();
		final FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
				.configure(params.xml()
						.setThrowExceptionOnMissing(false)
						.setEncoding("UTF-8")
						.setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
						.setValidating(false)
						.setFileName("./src/test/resources/junit-metrics.xml"));
		final XMLConfiguration config = builder.getConfiguration();
		AppMetrics.initFromConfig(config);
	}




	@Ignore
	@Test
	public void testIndependentIncrement()
	{
		try
		{
			initMetricsRepo();

			TsfCounter counter = new TsfCounter("test-independent");
			counter.incrSuccess();
			counter.incrFail();
			counter.incrTotal();


			assertEquals(1.0, counter.getSuccess().count(), 0.00);
			assertEquals(1.0, counter.getFail().count(), 0.00);
			assertEquals(1.0, counter.getTotal().count(), 0.00);
		}
		catch (ConfigurationException ex_)
		{
			fail(ex_.toString());
		}
	}




	
	@Test
	public void testDependentIncrement()
	{

		try
		{
			initMetricsRepo();


			TsfCounter counter = new TsfCounter("test-dependent", true);
			counter.incrSuccess();
			counter.incrFail();


			assertEquals(1.0, counter.getSuccess().count(), 0.00);
			assertEquals(1.0, counter.getFail().count(), 0.00);
			assertEquals(2.0, counter.getTotal().count(), 0.00);
		}
		catch (ConfigurationException ex_)
		{
			fail(ex_.toString());
		}
	}

}
