import java.io.FileReader;

@Grab(group='com.google.code.gson', module='gson', version='2.3.1')
import com.google.gson.GsonBuilder;


def parsed = new GsonBuilder().create().fromJson(new FileReader("java_data.json"), Post[].class);

class Post{
    String title;
    String link;
    String author;
    Set<String> hubs;
    int viewed;
    int favorite;
    int dayOfWeek;
    Integer voting;
    int hour;
    Date date;
}
