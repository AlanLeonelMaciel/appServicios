package com.ServiceMatch.SM.services;

import com.ServiceMatch.SM.entities.Provider;
import com.ServiceMatch.SM.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ProviderRepository providerRepository;

    public Optional<Provider> getProviderById(Long id) {
        return providerRepository.findById(id);
    }
}
