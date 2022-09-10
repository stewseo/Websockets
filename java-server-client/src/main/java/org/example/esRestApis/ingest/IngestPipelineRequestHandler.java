package org.example.esRestApis.ingest;

import org.example.esRestApis.AbstractApiHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class IngestPipelineRequestHandler extends AbstractApiHandler {
    private static Logger logger = LoggerFactory.getLogger(IngestPipelineRequestHandler.class);
    private String ingestEndpoint = "/_ingest";
    private List<String> pipelineIds;
    public IngestPipelineRequestHandler() throws Exception {}

    public String get(String method, String endpoint) throws Exception {
        if(method == null || endpoint == null) {
//            logger.debug("method or endpoint null{}", endpoint);
            throw new Exception("Throw exception");
        }
        String getIngestPipeline = String.format(ingestEndpoint, endpoint);
        return sendRequest(method, getIngestPipeline);
    }

    public String get(String endpoint) throws Exception {
        if(endpoint == null) {
            logger.info("method or endpoint null{}", endpoint);
            throw new Exception("Throw exception");
        }
        // TODO: Fix inheritance
        String getIngestPipeline = String.format("-X GET %s%s", super.getAliasedUrl(), endpoint);
        logger.info("passing request to AbstractApiHandler.sendRequest() {}", getIngestPipeline);
        return sendRequest("-X GET", getIngestPipeline);
    }

    // Creates or updates an ingest pipeline if it does not already exist
    public boolean createIfNotExists() throws IOException {
        String getIngestPipeline = String.format("-I %s%s", super.getAliasedUrl(), "/my-data-stream?pretty");

        String createOrUpdatePipeline = "PUT _ingest/pipeline/%s";
        String[] processors;
        String[] fields;
        String[] descriptions;
        String[] values;
        // {
        //  "kibana_sample_data_ecommerce": {
        //    "aliases": {},
        //    "mappings": {
        //      "properties": {
        //        "category": {
        //          "type": "text",
        //          "fields": {
        //            "keyword": {
        //              "type": "keyword"
        //            }
        //          }
        //        },
        //        "currency": {
        //          "type": "keyword"
        //        },
        //        "customer_birth_date": {
        //          "type": "date"
        //        },
        //        "customer_first_name": {
        //          "type": "text",
        //          "fields": {
        //            "keyword": {
        //              "type": "keyword",
        //              "ignore_above": 256
        //            }
        //          }
        //        },
        //        "customer_full_name": {
        //          "type": "text",
        //          "fields": {
        //            "keyword": {
        //              "type": "keyword",
        //              "ignore_above": 256
        //            }
        //          }
        //        },
        //        "customer_gender": {
        //          "type": "keyword"
        //        },
        //        "customer_id": {
        //          "type": "keyword"
        //        },
        //        "customer_last_name": {
        //          "type": "text",
        //          "fields": {
        //            "keyword": {
        //              "type": "keyword",
        //              "ignore_above": 256
        //            }
        //          }
        //        },
        //        "customer_phone": {
        //          "type": "keyword"
        //        },
        //        "day_of_week": {
        //          "type": "keyword"
        //        },
        //        "day_of_week_i": {
        //          "type": "integer"
        //        },
        //        "email": {
        //          "type": "keyword"
        //        },
        //        "event": {
        //          "properties": {
        //            "dataset": {
        //              "type": "keyword"
        //            }
        //          }
        //        },
        //        "geoip": {
        //          "properties": {
        //            "city_name": {
        //              "type": "keyword"
        //            },
        //            "continent_name": {
        //              "type": "keyword"
        //            },
        //            "country_iso_code": {
        //              "type": "keyword"
        //            },
        //            "location": {
        //              "type": "geo_point"
        //            },
        //            "region_name": {
        //              "type": "keyword"
        //            }
        //          }
        //        },
        //        "manufacturer": {
        //          "type": "text",
        //          "fields": {
        //            "keyword": {
        //              "type": "keyword"
        //            }
        //          }
        //        },
        //        "order_date": {
        //          "type": "date"
        //        },
        //        "order_id": {
        //          "type": "keyword"
        //        },
        //        "products": {
        //          "properties": {
        //            "_id": {
        //              "type": "text",
        //              "fields": {
        //                "keyword": {
        //                  "type": "keyword",
        //                  "ignore_above": 256
        //                }
        //              }
        //            },
        //            "base_price": {
        //              "type": "half_float"
        //            },
        //            "base_unit_price": {
        //              "type": "half_float"
        //            },
        //            "category": {
        //              "type": "text",
        //              "fields": {
        //                "keyword": {
        //                  "type": "keyword"
        //                }
        //              }
        //            },
        //            "created_on": {
        //              "type": "date"
        //            },
        //            "discount_amount": {
        //              "type": "half_float"
        //            },
        //            "discount_percentage": {
        //              "type": "half_float"
        //            },
        //            "manufacturer": {
        //              "type": "text",
        //              "fields": {
        //                "keyword": {
        //                  "type": "keyword"
        //                }
        //              }
        //            },
        //            "min_price": {
        //              "type": "half_float"
        //            },
        //            "price": {
        //              "type": "half_float"
        //            },
        //            "product_id": {
        //              "type": "long"
        //            },
        //            "product_name": {
        //              "type": "text",
        //              "fields": {
        //                "keyword": {
        //                  "type": "keyword"
        //                }
        //              },
        //              "analyzer": "english"
        //            },
        //            "quantity": {
        //              "type": "integer"
        //            },
        //            "sku": {
        //              "type": "keyword"
        //            },
        //            "tax_amount": {
        //              "type": "half_float"
        //            },
        //            "taxful_price": {
        //              "type": "half_float"
        //            },
        //            "taxless_price": {
        //              "type": "half_float"
        //            },
        //            "unit_discount_amount": {
        //              "type": "half_float"
        //            }
        //          }
        //        },
        //        "sku": {
        //          "type": "keyword"
        //        },
        //        "taxful_total_price": {
        //          "type": "half_float"
        //        },
        //        "taxless_total_price": {
        //          "type": "half_float"
        //        },
        //        "total_quantity": {
        //          "type": "integer"
        //        },
        //        "total_unique_products": {
        //          "type": "integer"
        //        },
        //        "type": {
        //          "type": "keyword"
        //        },
        //        "user": {
        //          "type": "keyword"
        //        }
        //      }
        //    },
        //    "settings": {
        //      "index": {
        //        "routing": {
        //          "allocation": {
        //            "include": {
        //              "_tier_preference": "data_content"
        //            }
        //          }
        //        },
        //        "number_of_shards": "1",
        //        "auto_expand_replicas": "0-1",
        //        "provided_name": "kibana_sample_data_ecommerce",
        //        "creation_date": "1662607612795",
        //        "number_of_replicas": "1",
        //        "uuid": "xVxT1y1oQ6O61mvsl0U-2g",
        //        "version": {
        //          "created": "8040199"
        //        }
        //      }
        //    }
        //  }
        //}
        //PUT _ingest/pipeline/my-pipeline-id
        //{
        //  "description" : "My optional pipeline description",
        //  "processors" : [
        //    {
        //      "set" : {
        //        "description" : "My optional processor description",
        //        "field": "my-keyword-field",
        //        "value": "foo"
        //      }
        //    }
        //  ]
        //}
//        logger.info("logging send request {}", sendRequest(getIngestPipeline));
        return sendRequest("POST", getIngestPipeline) != null;
    }

    public boolean delete(String pipelineId) throws IOException {
        String deleteIngestPipeline = "/_ingest/pipeline/" + pipelineId;
        return sendRequest("DELETE", deleteIngestPipeline) == null;
    }

    public String getIngestEndpoint() {
        return ingestEndpoint;
    }

    public void setIngestEndpoint(String ingestEndpoint) {
        this.ingestEndpoint = ingestEndpoint;
    }

    public List<String> getPipelineIds() {
        return pipelineIds;
    }

    public void setPipelineIds(List<String> pipelineIds) {
        this.pipelineIds = pipelineIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IngestPipelineRequestHandler that)) return false;
        return Objects.equals(ingestEndpoint, that.ingestEndpoint) && Objects.equals(pipelineIds, that.pipelineIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingestEndpoint, pipelineIds);
    }

    @Override
    public String toString() {
        return "number of ingest pipelines: {}" + pipelineIds.size() + " ingest pipeline names: " + pipelineIds;
    }
}
