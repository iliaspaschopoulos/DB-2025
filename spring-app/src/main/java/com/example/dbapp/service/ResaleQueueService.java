package com.example.dbapp.service;

import com.example.dbapp.model.ResaleQueue;
import com.example.dbapp.repository.ResaleQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResaleQueueService {

    private final ResaleQueueRepository resaleQueueRepository;

    @Autowired
    public ResaleQueueService(ResaleQueueRepository resaleQueueRepository) {
        this.resaleQueueRepository = resaleQueueRepository;
    }

    public List<ResaleQueue> getAllResaleQueues() {
        return resaleQueueRepository.findAll();
    }

    public Optional<ResaleQueue> getResaleQueueById(Integer id) {
        return resaleQueueRepository.findById(id);
    }

    public ResaleQueue saveResaleQueue(ResaleQueue resaleQueue) {
        return resaleQueueRepository.save(resaleQueue);
    }

    public ResaleQueue updateResaleQueue(Integer id, ResaleQueue resaleQueueDetails) {
        ResaleQueue resaleQueue = resaleQueueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ResaleQueue not found with id " + id));

        resaleQueue.setTicket(resaleQueueDetails.getTicket());
        resaleQueue.setVisitor(resaleQueueDetails.getVisitor());
        resaleQueue.setQueuePosition(resaleQueueDetails.getQueuePosition());
        resaleQueue.setEntryTime(resaleQueueDetails.getEntryTime());

        return resaleQueueRepository.save(resaleQueue);
    }

    public void deleteResaleQueue(Integer id) {
        resaleQueueRepository.deleteById(id);
    }
}
