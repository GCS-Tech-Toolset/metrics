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





    public void incrSuccess(@NonNull final TsfCounter cntr_)
    {
        cntr_.incrSuccess();
    }





    public void incrFail(@NonNull final TsfCounter cntr_)
    {
        cntr_.incrFail();
    }



}
