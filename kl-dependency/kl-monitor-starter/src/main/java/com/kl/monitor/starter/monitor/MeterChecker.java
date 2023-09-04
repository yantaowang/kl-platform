package com.kl.monitor.starter.monitor;

import io.micrometer.core.instrument.Meter;

public interface MeterChecker {

    boolean check(String type, Meter meter);

}
