package com.kl.monitor.starter.monitor;

import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorMetricsEntity {

    private String name;
    private String description;
    private Set<Tag> tags;

    public MonitorMetricsEntity addTag(String name, String value) {
        if (tags == null) {
            tags = new HashSet<>();
        }
        tags.add(Tag.of(name, value));
        return this;
    }
}
