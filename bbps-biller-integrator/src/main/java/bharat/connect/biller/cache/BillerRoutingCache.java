package bharat.connect.biller.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BillerRoutingCache {
    
    private final Map<String, String> fetchRouteCache = new ConcurrentHashMap<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        // Warm the cache on startup so we don't hit the DB for every fetch request
        jdbcTemplate.query("SELECT biller_id, mock_fetch_url FROM registered_billers", rs -> {
            fetchRouteCache.put(rs.getString("biller_id"), rs.getString("mock_fetch_url"));
        });
        System.out.println("=== BillerRoutingCache Warmed Up: " + fetchRouteCache.size() + " routes ===");
    }

    public String getFetchUrl(String billerId) {
        return fetchRouteCache.get(billerId);
    }

    public void addRoute(String billerId, String fetchUrl) {
        fetchRouteCache.put(billerId, fetchUrl);
        System.out.println("=== Added Route to Cache: " + billerId + " -> " + fetchUrl + " ===");
    }
}
