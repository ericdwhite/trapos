package whitewerx.com.trapos.model.event;

public interface EventHandler<T extends Event> {
    public void handle(T event);
}
