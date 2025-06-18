package com.jcs.fileservice.service.interfaces;

import com.jcs.fileservice.models.entity.ImageCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface IImageCategories {
   Page<ImageCategories> findAllCategories(Pageable pageable);
   Page<ImageCategories> findByName(String code, Pageable pageable);
   ImageCategories findByPublicId(UUID publicId);
   ImageCategories findByName(String code);
   ImageCategories create(ImageCategories imageCategories);
   ImageCategories update(UUID publicId, ImageCategories imageCategories);
   void  delete(UUID publicId);
}