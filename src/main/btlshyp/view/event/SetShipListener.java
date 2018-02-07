package main.btlshyp.view.event;

import java.util.EventListener;

public interface SetShipListener extends EventListener{
    public void setShipEventOccurred(SetShipEvent sse);
}
