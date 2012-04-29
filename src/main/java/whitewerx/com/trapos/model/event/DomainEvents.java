package whitewerx.com.trapos.model.event;

import java.util.HashMap;

/**
 * A way to handle notifications for events within the
 * domain.  This only allows for one handler for each
 * domain event.
 * 
 * Care should be taken about the transaction boundary
 * of {@link EventHandler}s.
 * 
 * http://www.udidahan.com/2009/06/14/domain-events-salvation/
 * 
 * @author ewhite
 */
public class DomainEvents {
    
    private static ThreadLocal<HashMap<Class<? extends Event>, EventHandler<? extends Event>>> handlers = 
            new ThreadLocal<HashMap<Class<? extends Event>, EventHandler<? extends Event>>>();

    public static void registerFor(Class<? extends Event> eventType, EventHandler<? extends Event> handler) {
        handlerMap().put(eventType, handler);       
    }
    
    public static void unregisterFor(Class<? extends Event> eventType) {
        handlerMap().remove(eventType);
    }
    
    public static void raise(Event event) {
        Class<? extends Event> eventType = event.getClass();
        
        @SuppressWarnings("unchecked")
        EventHandler<Event> handler = (EventHandler<Event>) handlerMap().get(eventType);
        
        if( handler==null )
            return;
        
        handler.handle(event);
    }
    
    public static HashMap<Class<? extends Event>, EventHandler<? extends Event>> handlerMap() {
        HashMap<Class<? extends Event>, EventHandler<? extends Event>> map = handlers.get();
        if(map!=null)
            return map;
        map = new HashMap<Class<? extends Event>, EventHandler<? extends Event>>();
        handlers.set(map);
        return map;
    }
}
