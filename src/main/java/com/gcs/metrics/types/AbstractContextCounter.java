/****************************************************************************
 * FILE: AbstractContextCounter.java
 * DSCRPT: 
 ****************************************************************************/





package com.gcs.metrics.types;





import java.util.Map;



import lombok.NonNull;





public abstract class AbstractContextCounter
{
    public abstract Map<String, Map<String, String>> toMap();


}
