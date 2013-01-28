/**
 * TownRanks is used by the Towns class for informational purposes
 * @author shieldsr
 */
public class TownRank {
    protected String rankName = "default";
    protected int rank = 0;
    protected int maxDeputies = 5;
    protected int minCitizens = 0;
    protected int maxCitizens = 100;
    protected double moneyCost = 100;
    protected int itemCost = 10;
    
    /**
     * The constructor is the only way to insert data to the class, called from Muni's main class
     * @author bobbshields
     */
    public TownRank (int id, String name, int max_Deputies, int min_Citizens, int max_Citizens, double money_Cost, int item_Cost){
        if ( id > 0 ) {rank = id;}
        if (!name.trim().contains("\\s")) {rankName=name; } // disallow spaces in the name
        if (max_Deputies > 0)  { maxDeputies = max_Deputies; }
        if (min_Citizens >= 0) { minCitizens = min_Citizens; }
        if (max_Citizens >= 0) { maxCitizens = max_Citizens; }
        if (money_Cost > 0 ) { moneyCost = money_Cost; }
        if (item_Cost > 0 ) { itemCost = item_Cost; }       
    }
    
    public String getName(){
        return rankName;
    }
    public int getRank(){
        return rank;
    }
    public int getMaxDeputies(){
        return maxDeputies;
    }
    public int getMinCitizens(){
        return minCitizens;
    }
    public int getMaxCitizens(){
        return maxCitizens;
    }
    public double getMoneyCost(){
        return moneyCost;
    }
    public int getItemCost(){
        return itemCost;
    }
    
}
