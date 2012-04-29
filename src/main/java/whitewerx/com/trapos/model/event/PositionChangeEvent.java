package whitewerx.com.trapos.model.event;

import whitewerx.com.trapos.model.Position;

/**
 * Sent when the position is updated due to a new trade being added to it or a
 * rate update.
 * 
 * See: {@link Position#notifyChange}
 * 
 * @author ewhite
 * 
 */
public class PositionChangeEvent implements Event {
    private Position changedPosition;

    public PositionChangeEvent(Position changed) {
        this.changedPosition = changed;
    }

    public Position getPosition() {
        return this.changedPosition;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changedPosition == null) ? 0 : changedPosition.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PositionChangeEvent other = (PositionChangeEvent) obj;
        if (changedPosition == null) {
            if (other.changedPosition != null)
                return false;
        } else if (!changedPosition.equals(other.changedPosition))
            return false;
        return true;
    }
}
