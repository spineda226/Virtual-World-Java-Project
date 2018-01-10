import processing.core.PImage;
import java.util.List;

public class Grave extends AbstractEntity
{
    public Grave(String id, Point position, List<PImage> images)
    {
        super(position, images, id);
    }

    public <R> R accept(EntityVisitor<R> visitor)
    {
        return visitor.visit(this);
    }
}
