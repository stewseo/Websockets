import org.example.esRestApis.index.IndexRequestHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// Test cases for ES Index Apis
class IndexRequestHandlerTest extends AbstractTestCase {

    public static IndexRequestHandler indexHandler;

    @BeforeAll
    static void setup(){
        indexHandler = new IndexRequestHandler();
    }
    @Test
    void indexDoesNotExistTest() throws Exception {
        String index = "test";
        assertFalse(indexHandler.exists(index));
    }
    @Test
    void indexExistsTest() throws Exception {
        String index = "filebeat-8.4.0";
        assertTrue(indexHandler.exists(index));
    }
    @Test
    void createIndexIfNotExistsTest() throws IOException {
        String testIndexId = "my-index00001";
        assertTrue(indexHandler.createIfNotExists(testIndexId));
    }

    @Test
    void getIndexTest() throws Exception {
        String testIndexMyIndex00001 = "my-index00001";
        assertEquals("test", indexHandler.getIndex(testIndexMyIndex00001));
        String testIndexId = "/kibana_sample_data_ecommerce";
        assertEquals("{\"kibana_sample_data_ecommerce\":{\"aliases\":{}," +
                        "\"mappings\":{\"properties\":{\"category\":{\"type\":\"text\"," +
                        "\"fields\":{\"keyword\":{\"type\":\"keyword\"}}}," +
                        "\"currency\":{\"type\":\"keyword\"}," +
                        "\"customer_birth_date\":{\"type\":\"date\"}," +
                        "\"customer_first_name\":{\"type\":\"text\"," +
                        "\"fields\":{\"keyword\":{\"type\":\"keyword\"," +
                        "\"ignore_above\":256}}},\"customer_full_name\":{\"type\":\"text\"," +
                        "\"fields\":{\"keyword\":{\"type\":\"keyword\"," +
                        "\"ignore_above\":256}}}," +
                        "\"customer_gender\":{\"type\":\"keyword\"}," +
                        "\"customer_id\":{\"type\":\"keyword\"}," +
                        "\"customer_last_name\":{\"type\":\"text\"," +
                        "\"fields\":{\"keyword\":{\"type\":\"keyword\"," +
                        "\"ignore_above\":256}}},\"customer_phone\":{\"type\":\"keyword\"}," +
                        "\"day_of_week\":{\"type\":\"keyword\"},\"day_of_week_i\":{\"type\":\"integer\"}," +
                        "\"email\":{\"type\":\"keyword\"},\"event\":{\"properties\":{\"dataset\":{\"type\":\"keyword\"}}},\"geoip\":{\"properties\":{\"city_name\":{\"type\":\"keyword\"}," +
                        "\"continent_name\":{\"type\":\"keyword\"}," +
                        "\"country_iso_code\":{\"type\":\"keyword\"}," +
                        "\"location\":{\"type\":\"geo_point\"}," +
                        "\"region_name\":{\"type\":\"keyword\"}}},\"manufacturer\":{\"type\":\"text\"," +
                        "\"fields\":{\"keyword\":{\"type\":\"keyword\"}}},\"order_date\":{\"type\":\"date\"}," +
                        "\"order_id\":{\"type\":\"keyword\"},\"products\":{\"properties\":{\"_id\":{\"type\":\"text\"," +
                        "\"fields\":{\"keyword\":{\"type\":\"keyword\"," +
                        "\"ignore_above\":256}}},\"base_price\":{\"type\":\"half_float\"},\"base_unit_price\":{\"type\":\"half_float\"}," +
                        "\"category\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\"}}}," +
                        "\"created_on\":{\"type\":\"date\"}," +
                        "\"discount_amount\":{\"type\":\"half_float\"}," +
                        "\"discount_percentage\":{\"type\":\"half_float\"}," +
                        "\"manufacturer\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\"}}},\"min_price\":{\"type\":\"half_float\"}," +
                        "\"price\":{\"type\":\"half_float\"},\"product_id\":{\"type\":\"long\"},\"product_name\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\"}},\"analyzer\":\"english\"},\"quantity\":{\"type\":\"integer\"},\"sku\":{\"type\":\"keyword\"},\"tax_amount\":{\"type\":\"half_float\"},\"taxful_price\":{\"type\":\"half_float\"},\"taxless_price\":{\"type\":\"half_float\"},\"unit_discount_amount\":{\"type\":\"half_float\"}}},\"sku\":{\"type\":\"keyword\"},\"taxful_total_price\":{\"type\":\"half_float\"},\"taxless_total_price\":{\"type\":\"half_float\"},\"total_quantity\":{\"type\":\"integer\"},\"total_unique_products\":{\"type\":\"integer\"},\"type\":{\"type\":\"keyword\"},\"user\":{\"type\":\"keyword\"}}},\"settings\":{\"index\":{\"routing\":{\"allocation\":{\"include\":{\"_tier_preference\":\"data_content\"}}},\"number_of_shards\":\"1\",\"auto_expand_replicas\":\"0-1\"," +
                        "\"provided_name\":\"kibana_sample_data_ecommerce\",\"creation_date\":\"1662607612795\",\"number_of_replicas\":\"1\",\"uuid\":\"xVxT1y1oQ6O61mvsl0U-2g\",\"version\":{\"created\":\"8040199\"}}}}}",
                indexHandler.getIndex(testIndexId));
    }
    // Test creating List<String> indexTemplates
    @Test
    void createIfNotExistListOfIndexTemplates(){
    }




}