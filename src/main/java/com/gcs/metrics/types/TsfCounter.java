/****************************************************************************
 * FILE: TSFCounter.java
 * DSCRPT: 
 ****************************************************************************/





package com.gcs.metrics.types;





import java.util.HashMap;
import java.util.Map;



import com.gcs.metrics.AppMetrics;



import io.micrometer.core.instrument.Counter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;





@Slf4j
@Data
public class TsfCounter
{
	private final boolean	_autoIncrement;
	private final String	_baseName;
	private Counter			_total;
	private Counter			_success;
	private Counter			_fail;



	public TsfCounter(String name_)
	{
		this(name_, false);
	}





	/**
	 * @param name:
	 *            base name
	 * 
	 * @param auto-increment:
	 *            increment the total with each 'success' or 'fail' increment
	 */
	public TsfCounter(String name_, boolean autoIncrement_)
	{
		_baseName = name_;
		_autoIncrement = autoIncrement_;
		_total = AppMetrics.getInstance().createCounter(_baseName + "-total");
		_success = AppMetrics.getInstance().createCounter(_baseName + "-success");
		_fail = AppMetrics.getInstance().createCounter(_baseName + "-fail");
	}





	public void incrTotal()
	{
		if (_autoIncrement)
		{
			return;
		}
		incr(_total);
	}





	public void incrSuccess()
	{
		incr(_success);
		if (_autoIncrement)
		{
			incr(_total);
		}
	}





	public void incrFail()
	{
		incr(_fail);
		if (_autoIncrement)
		{
			incr(_total);
		}
	}





	final private void incr(Counter counter_)
	{
		if (counter_ == null)
		{
			_logger.debug("attempting to increment a NULL counter");
			return;
		}
		counter_.increment();
	}





	@Override
	public String toString()
	{
		StringBuilder buff = new StringBuilder(_baseName);
		buff.append(",total=");
		buff.append(Double.toString(_total.count()));
		buff.append(",success=");
		buff.append(Double.toString(_success.count()));
		buff.append(",fail=");
		buff.append(Double.toString(_fail.count()));
		return buff.toString();
	}





	public Map<String, Map<String, String>> toMap()
	{
		var map = new HashMap<String, Map<String, String>>();
		var vals = new HashMap<String, String>();
		vals.put("total", Double.toString(_total.count()));
		vals.put("success", Double.toString(_success.count()));
		vals.put("fail", Double.toString(_fail.count()));
		map.put(_baseName, vals);
		return map;
	}





}
