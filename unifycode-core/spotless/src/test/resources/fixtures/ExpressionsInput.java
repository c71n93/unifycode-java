package fixture;
import java.util.List;
final class ExpressionsFixture{
private String rowToCsv(final List<String> row){
return row.stream().map(this::escape).reduce((left,right)->left+","+right).orElse("");
}
private String escape(final String value){
final boolean quoted=value.contains(",")||value.contains("\"")||value.contains("\n");
if(!quoted){return value;}
return "\"" + value.replace("\"","\"\"")+"\"";
}
}
