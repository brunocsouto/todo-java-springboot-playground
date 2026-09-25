/* ?:
 * <<CONTROLLER>>
 *
 * INFO Layer:
 *   HTTP request handling and presentation.
 *
 * INFO Interface:
 *   Defines the HTTP communication boundary between external clients
 *   and the application.
 *
 * INFO Responsibility:
 *   Receive requests, delegate operations to the Service layer,
 *   and return appropriate HTTP responses.
 *
 * INFO The HTTP API is the communication boundary;
 *   the controller layer is the presentation responsibility boundary.
 */
package dev.souto.v0.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    //TODO Define HTTP endpoints and request handling
}
