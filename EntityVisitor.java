public interface EntityVisitor<R>
{
    R visit(MinerNotFull minerNotFull);
    R visit(MinerFull minerFull);
    R visit(OreBlob oreBlob);
    R visit(Quake quake);
    R visit(Vein vein);
    R visit(Ore ore);
    R visit(Obstacle obstacle);
    R visit(Blacksmith blacksmith);
    R visit(Bee bee);
    R visit(Grave grave);
    R visit(Butterfly butterfly);
}
