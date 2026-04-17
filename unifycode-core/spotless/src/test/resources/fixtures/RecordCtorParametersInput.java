package fixtures;
import java.util.List;
public class RecordCtorParametersExpected{
public sealed interface MemoryEvents permits RecordCtorParametersExpected.AvailableMemoryEvents{
String storeEventName();
List<String> loadMetricNames();
List<String> storeMetricNames();
String eventNames();
}
public record AvailableMemoryEvents(String loadEventName, String storeEventName, List<String> loadMetricNames, List<String> storeMetricNames) implements MemoryEvents{
public AvailableMemoryEvents{
loadMetricNames=List.copyOf(loadMetricNames);
storeMetricNames=List.copyOf(storeMetricNames);
}
public AvailableMemoryEvents(final String loadEventName, final String storeEventName){
this(loadEventName,storeEventName,List.of(loadEventName),List.of(storeEventName));
}
@Override
public String eventNames(){
return this.loadEventName + "," + this.storeEventName;
}
}
}
