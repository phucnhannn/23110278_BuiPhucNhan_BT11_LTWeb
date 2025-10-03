package vn.iotstar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.model.Customer;
import vn.iotstar.repository.UserInfoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> list = userInfoRepository.findAll().stream()
                .map(u -> Customer.builder()
                        .id(String.valueOf(u.getId()))
                        .name(u.getName())
                        .phoneNumber(null)
                        .email(u.getEmail())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") String id) {
        try {
            Integer intId = Integer.valueOf(id);
            return userInfoRepository.findById(intId)
                    .map(u -> Customer.builder()
                            .id(String.valueOf(u.getId()))
                            .name(u.getName())
                            .phoneNumber(null)
                            .email(u.getEmail())
                            .build())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}