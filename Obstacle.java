import processing.core.PImage;
import java.util.List;

public class Obstacle extends AbstractEntity
{
    public Obstacle(String id, Point position, List<PImage> images)
    {
        super(position, images, id);
    }

    public <R> R accept(EntityVisitor<R> visitor)
    {
        return visitor.visit(this);
    }
}
