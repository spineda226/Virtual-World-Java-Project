public class BeeAttackVisitor extends AllFalseEntityVisitor
{
    public Boolean visit(MinerNotFull minerNotFull) { return true; }
    public Boolean visit(MinerFull minerFull) { return true; }
    public Boolean visit(OreBlob oreBlob) { return true; }
    public Boolean visit(Butterfly butterfly) { return true; }
    public Boolean visit(Blacksmith blacksmith) { return true; }
}
