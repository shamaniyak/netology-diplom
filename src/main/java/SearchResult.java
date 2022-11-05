import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.util.List;

public class SearchResult {
    private final List<PageEntry> pages;

    public SearchResult(List<PageEntry> pages) {
        this.pages = pages;
    }

    public String toJson() {
        if(pages == null || pages.isEmpty())
            return "Ничего не найдено";
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        JsonArray jsArray = new JsonArray(pages.size());
        for(var pageEntry : pages) {
            jsArray.add(gson.toJsonTree(pageEntry));
        }
        return gson.toJson(jsArray);
    }
}
