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
package org.craftercms.deployer.impl.processors;

import java.io.IOException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.craftercms.deployer.api.Deployment;
import org.craftercms.deployer.api.ProcessorExecution;
import org.craftercms.deployer.api.exceptions.DeploymentException;
import org.craftercms.deployer.utils.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alfonsovasquez on 12/27/16.
 */
public class HttpMethodCallProcessor extends AbstractMainDeploymentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HttpMethodCallProcessor.class);

    public static final String URL_CONFIG_KEY = "url";
    public static final String METHOD_CONFIG_KEY = "method";

    protected String url;
    protected String method;
    protected CloseableHttpClient httpClient;

    @Override
    public void destroy() throws DeploymentException {
        IOUtils.closeQuietly(httpClient);
    }

    @Override
    protected void doInit(Configuration mainConfig, Configuration processorConfig) throws DeploymentException {
        url = ConfigurationUtils.getRequiredString(processorConfig, URL_CONFIG_KEY);
        method = ConfigurationUtils.getRequiredString(processorConfig, METHOD_CONFIG_KEY);
        httpClient = HttpClients.createDefault();
    }

    @Override
    protected void doExecute(Deployment deployment, ProcessorExecution execution) throws DeploymentException {
        HttpUriRequest request = createRequest();

        logger.info("Executing request {}...", request);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String body = entity != null? EntityUtils.toString(entity) : null;

            if (StringUtils.isEmpty(body)) {
                body = "empty";
            }

            if (status >= 200 && status < 300) {
                logger.info("Successful response for request {}: status = {}, body = {}", request, status, body);

                execution.setStatusDetails("Successful response for request " + request + ": status = " + status);
            } else {
                logger.error("Error response for request {}: status = {}, body = {}", request, status, body);

                execution.setStatusDetails("Error response for request " + request + ": status = " + status);
                execution.endExecution(Deployment.Status.FAILURE);
            }
        } catch (IOException e) {
            throw new DeploymentException("IO error on HTTP request " + request, e);
        }
    }

    @Override
    protected boolean failDeploymentOnProcessorFailure() {
        return false;
    }

    protected HttpUriRequest createRequest() throws DeploymentException {
        if (method.equalsIgnoreCase("get")) {
            return new HttpGet(url);
        } else if (method.equalsIgnoreCase("post")) {
            return new HttpPost(url);
        } else if (method.equalsIgnoreCase("put")) {
            return new HttpPut(url);
        } else if (method.equalsIgnoreCase("delete")) {
            return new HttpDelete(url);
        } else if (method.equalsIgnoreCase("head")) {
            return new HttpHead(url);
        } else if (method.equalsIgnoreCase("options")) {
            return new HttpOptions(url);
        } else if (method.equalsIgnoreCase("trace")) {
            return new HttpTrace(url);
        } else {
            throw new DeploymentException("HTTP method '" + method + " not recognized");
        }
    }

}
