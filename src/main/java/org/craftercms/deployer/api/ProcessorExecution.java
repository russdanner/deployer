/*
 * Copyright (C) 2007-2016 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.deployer.api;

import java.time.ZonedDateTime;

import static org.craftercms.deployer.api.Deployment.Status;

/**
 * Created by alfonsovasquez on 12/29/16.
 */
public class ProcessorExecution {

    protected String processorName;
    protected volatile ZonedDateTime start;
    protected volatile ZonedDateTime end;
    protected volatile Status status;
    protected volatile Object statusDetails;

    public ProcessorExecution(String processorName) {
        this.processorName = processorName;
        this.start = ZonedDateTime.now();
    }

    public String getProcessorName() {
        return processorName;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public boolean isRunning() {
        return end == null;
    }

    public Status getStatus() {
        return status;
    }

    public Object getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(Object statusDetails) {
        this.statusDetails = statusDetails;
    }

    public void endExecution(Status status) {
        if (isRunning()) {
            this.end = ZonedDateTime.now();
            this.status = status;
        }
    }

}
