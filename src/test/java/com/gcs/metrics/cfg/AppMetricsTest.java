/****************************************************************************
 * FILE: AppMetricsTest.java
 * DSCRPT: 
 ****************************************************************************/





package com.gcs.metrics.cfg;





import com.gcs.metrics.AppMetrics;
import com.gcs.metrics.cfg.properties.InfluxMetricsProps;



import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;



import lombok.extern.slf4j.Slf4j;





@Slf4j
public class AppMetricsTest
{

    @Test
    public void test()
    {
        try
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
            AppMetrics metrics = AppMetrics.initFromConfig(config);
            Assert.assertEquals(metrics.getMetricsConfig().isEnabled(), true);
            Assert.assertEquals(((InfluxMetricsProps)metrics.getMetricsConfig()).getBatchSize(), 1000);
            Assert.assertEquals(((InfluxMetricsProps)metrics.getMetricsConfig()).getDbUsername(), "ee6SHf2cfaEgkYpJsQNZHUSPvpJ81ScU");
        }
        catch (ConfigurationException ex_)
        {
            _logger.error(ex_.toString(), ex_);
        }
    }
}
