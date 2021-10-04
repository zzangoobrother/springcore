package com.sparta.springcore.service;

import com.sparta.springcore.model.Folder;
import com.sparta.springcore.model.Product;
import com.sparta.springcore.model.User;
import com.sparta.springcore.repository.FolderRepository;
import com.sparta.springcore.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final ProductRepository productRepository;

    public FolderService(FolderRepository folderRepository, ProductRepository productRepository) {
        this.folderRepository = folderRepository;
        this.productRepository = productRepository;
    }

    public List<Folder> addFolders(List<String> folderNames, User user) {
        List<Folder> findFolderList = folderRepository.findAllByUserAndNameIn(user, folderNames);

        List<Folder> folders = new ArrayList<>();
        for (String folderName : folderNames) {
            if (folders.stream().anyMatch(f -> f.getName().equals(folderName))) {
                continue;
            }

            if (!isSameFolderName(folderName, findFolderList)) {
                Folder folder = new Folder(folderName.trim(), user);
                folders.add(folder);
            }
        }

        return folderRepository.saveAll(folders);
    }

    private boolean isSameFolderName(String folderName, List<Folder> findFolderList) {
        for (Folder folder : findFolderList) {
            if (folder.getName().equals(folderName)) {
                return true;
            }
        }
        return false;
    }

    public List<Folder> getFolders(User user) {
        return folderRepository.findAllByUser(user);
    }

    public Page<Product> getProductsInFolder(Long folderId, int page, int size, String sortBy, boolean isAsc, User user) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        page--;
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAllByUserIdAndFolderList_Id(user.getId(), folderId, pageable);
    }
}
