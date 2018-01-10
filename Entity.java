import processing.core.PImage;

public interface Entity
{
   Point getPosition();

   void setPosition(Point position);

   PImage getCurrentImage();

   <R> R accept (EntityVisitor<R> visitor);
}

