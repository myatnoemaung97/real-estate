package com.example.Online.Housing.Show.controllers;

import com.example.Online.Housing.Show.models.*;
import com.example.Online.Housing.Show.repositories.*;
import com.example.Online.Housing.Show.services.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HousingController {

    private final HousingRepository houseRepo;
    private final DetailImageRepository detailImageRepository;
    private final ImageStorageService storageService;
    private final AccountRepository accRepo;
    private final ModelMapper mapper;
    private final HousingDAO housingDAO;
    private final WishListRepository wishListRepository;

    @GetMapping("/account/{id}/addHousing")
    public ModelAndView addHousing(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("housing-form");
        Housing housing = new Housing();
        housing.setOwnerId(id);
        mav.addObject("housingTypes", Arrays.asList("Apartment", "House", "Condo", "Penthouse"));
        mav.addObject("housing", housing);
        mav.addObject("detailImages", new DetailImage());
        mav.addObject("accountId", id);
        return mav;
    }

    @PostMapping("/account/{ownerId}/saveHousing")
    public String saveHousing(
            @ModelAttribute Housing housing,
            @PathVariable Long ownerId,
            @RequestParam("image") MultipartFile mainImage,
            @RequestParam(value = "moreImages", required = false) MultipartFile[] moreImages) throws IOException {

        if (housing.getId() == null) {
            housing.setCreatedDate(LocalDateTime.now());
        } else {
            housing.setUpdatedDate(LocalDateTime.now());
        }

        String imageName = ownerId + ".jpg";
        if (!mainImage.isEmpty()) {
            File file = convertMultipartFileToFile(mainImage);
            String imageUrl = storageService.uploadImage(file, imageName, "housing-images");
            housing.setImageUrl(imageUrl);
            file.delete();
        }

        housing.setOwnerId(ownerId);
        Housing savedHousing = houseRepo.save(housing);

        System.out.println(moreImages.length);

        if (moreImages != null) {
            for (MultipartFile image : moreImages) {
                if (!image.isEmpty()) {
                    imageName = savedHousing.getId() + ".jpg";
                    File file = convertMultipartFileToFile(image);
                    String imageUrl = storageService.uploadImage(file, imageName, "housing-images");
                    DetailImage detailImage = new DetailImage();
                    detailImage.setHousingId(savedHousing.getId());
                    detailImage.setUrl(imageUrl);
                    detailImageRepository.save(detailImage);
                    file.delete();
                }
            }
        }
        return "redirect:/account/{ownerId}/home";
    }

    @GetMapping("/account/{accountId}/housings")
    public ModelAndView showAllHousings(@PathVariable Long accountId) {
        ModelAndView mav = new ModelAndView("all-housings");
        List<Housing> housings = houseRepo.findAll();
        List<HousingWithOwnerInfo> housingCards = new ArrayList<>();
        for (Housing housing: housings) {
            HousingWithOwnerInfo housingWithOwnerInfo = mapper.map(housing, HousingWithOwnerInfo.class);
            Account owner = accRepo.findById(housing.getOwnerId()).get();
            housingWithOwnerInfo.setOwnerName(owner.getName());
            housingWithOwnerInfo.setOwnerEmail(owner.getEmail());
            housingWithOwnerInfo.setOwnerPhone(owner.getPhone());
            housingCards.add(housingWithOwnerInfo);
        }
        mav.addObject("housingCards", housingCards);
        mav.addObject("accountId", accountId);
        return mav;
    }

    @PostMapping("/account/{accountId}/searchHousing")
    public ModelAndView searchHousing(
            @PathVariable Long accountId,
            @ModelAttribute SearchRequest searchRequest
    ) {
        List<Housing> housings = housingDAO.findHousingsWithCriteria(searchRequest);
        ModelAndView mav = showSearchedHousings(housings);
        return mav;
    }

    public ModelAndView showSearchedHousings(List<Housing> housings) {
        ModelAndView mav = new ModelAndView("all-housings");
        List<HousingWithOwnerInfo> housingCards = new ArrayList<>();
        for (Housing housing: housings) {
            HousingWithOwnerInfo housingWithOwnerInfo = mapper.map(housing, HousingWithOwnerInfo.class);
            Account owner = accRepo.findById(housing.getOwnerId()).get();
            housingWithOwnerInfo.setOwnerName(owner.getName());
            housingWithOwnerInfo.setOwnerEmail(owner.getEmail());
            housingWithOwnerInfo.setOwnerPhone(owner.getPhone());
            housingCards.add(housingWithOwnerInfo);
        }
        mav.addObject("housingCards", housingCards);
        return mav;
    }

    @GetMapping("/{housingId}/moreImages")
    public ModelAndView moreImages(@PathVariable Long housingId) {
        ModelAndView mav = new ModelAndView("more-images");
        Housing housing = houseRepo.findById(housingId).get();
        List<DetailImage> detailImages = detailImageRepository.findByHousingId(housingId);
        mav.addObject("detailImages", detailImages);
        mav.addObject("mainImageUrl", housing.getImageUrl());
        return mav;
    }

    @GetMapping("/account/{accountId}/editHousing/{housingId}")
    public ModelAndView editHousing(@PathVariable Long housingId) {
        ModelAndView mav = new ModelAndView("edit-housing");
        Housing housing = houseRepo.findById(housingId).get();
        mav.addObject("housing", housing);
        mav.addObject("housingTypes", getHousingTypes());
        return mav;
    }

    @GetMapping("/account/{accountId}/deleteHousing/{housingId}")
    public String deleteHousing(@PathVariable Long housingId) {
        houseRepo.deleteById(housingId);
        return "redirect:/account/{accountId}/home";
    }

    @GetMapping("/{housingId}/images")
    public ModelAndView showImages(@PathVariable Long housingId) {
        ModelAndView mav = new ModelAndView("images");
        List<DetailImage> images = detailImageRepository.findByHousingId(housingId);
        System.out.println(images.size());
        Housing housing = houseRepo.findById(housingId).get();
        mav.addObject("images", images);
        mav.addObject("housing", housing);
        return mav;
    }

    @GetMapping("/{housingId}/{imageId}/delete")
    public String deleteImage(
            @PathVariable Long imageId,
            @PathVariable Long housingId) {
        detailImageRepository.deleteById(imageId);
        return "redirect:/{housingId}/images";
    }

    @GetMapping("/{accountId}/fav/{housingId}")
    public String favHousing(
            @PathVariable Long accountId,
            @PathVariable Long housingId
    ) {
        List<WishList> wishLists = wishListRepository.findByAccountId(accountId);
        for (WishList wishList: wishLists) {
            if (wishList.getHousingId().equals(housingId)) {
                return "redirect:/account/{accountId}/housings";
            }
        }

        WishList wishList = new WishList(accountId, housingId);
        wishListRepository.save(wishList);
        return "redirect:/account/{accountId}/housings";
    }

    @GetMapping("/account/{accountId}/favourites")
    public ModelAndView favouriteHousings(@PathVariable Long accountId) {
        List<Housing> housings = new ArrayList<>();
        List<WishList> wishLists = wishListRepository.findByAccountId(accountId);
        for (WishList wishList: wishLists) {
            Housing housing = houseRepo.findById(wishList.getHousingId()).get();
            housings.add(housing);
        }
        return showSearchedHousings(housings);
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        System.out.println(file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }

    private List<String> getHousingTypes() {
        return Arrays.asList("Apartment", "House", "Condo", "Penthouse");
    }




}
