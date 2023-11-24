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
public class AdminController {

    private final HousingRepository housingRepo;
    private final DetailImageRepository detailImageRepository;
    private final ImageStorageService storageService;
    private final AccountRepository accRepo;
    private final ModelMapper mapper;
    private final HousingDAO housingDAO;
    private final WishListRepository wishListRepository;

    @GetMapping("/admin/{accountId}/home")
    public ModelAndView adminHome(@PathVariable Long accountId) {
        ModelAndView mav = new ModelAndView("admin-home");
        List<Account> accounts = accRepo.findAll();
        Integer numberOfAccounts = accounts.size();
        Integer numberOfHousings = housingRepo.findAll().size();
        Account adminAccount = accRepo.findById(accountId).get();
        mav.addObject("adminAccount", adminAccount);
        mav.addObject("accounts", accounts);
        mav.addObject("numberOfAccounts", numberOfAccounts);
        mav.addObject("numberOfHousings", numberOfHousings);
        return mav;
    }

    @GetMapping("/admin/{id}/edit")
    public ModelAndView editAccount1(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("admin-contact-edit");
        Account account = accRepo.findById(id).get();
        System.out.println(account.getPassword());
        mav.addObject("account", account);
        return mav;
    }

    @PostMapping("/admin/{id}/updateAccount")
    public String updateAccount(@PathVariable Long id, @ModelAttribute Account account) {
        accRepo.updateAccount(account.getName(), account.getEmail(), account.getPhone(), id);
        return "redirect:/admin/{id}/home";
    }

    @GetMapping("/admin/{adminId}/{accountId}/delete")
    public String deleteAccount(@PathVariable Long accountId) {
        accRepo.deleteById(accountId);
        return "redirect:/admin/{adminId}/home";
    }

    @GetMapping("/admin/{accountId}")
    public ModelAndView editAccount(@PathVariable Long accountId) {
        ModelAndView mav = new ModelAndView("account-home-page");
        Account account = accRepo.findById(accountId).get();
        mav.addObject("account", account);
        List<Housing> ownHousings = housingRepo.findByOwnerId(accountId);
        mav.addObject("ownHousings", ownHousings);
        return mav;
    }

    @GetMapping("/admin/{accountId}/details")
    public ModelAndView accountDetails(@PathVariable Long accountId) {
        ModelAndView mav = new ModelAndView("account-details");
        Account account = accRepo.findById(accountId).get();
        mav.addObject("account", account);
        List<Housing> housings = housingRepo.findByOwnerId(accountId);
        List<DetailImage> images = new ArrayList<>();
        for (Housing housing: housings) {
            List<DetailImage> detailImages = detailImageRepository.findByHousingId(housing.getId());
            images.addAll(detailImages);
        }
        mav.addObject("images", images);
        return mav;
    }

    @GetMapping("/admin/{accountId}/details/{imageId}/delete")
    public String imageDelete(
            @PathVariable Long accountId,
            @PathVariable Long imageId
    ) {
        detailImageRepository.deleteById(imageId);
        return "redirect:/admin/{accountId}/details";
    }

    @GetMapping("/admin/{id}/addHousing")
    public ModelAndView addHousing(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("admin-housing-form");
        Housing housing = new Housing();
        housing.setOwnerId(id);
        mav.addObject("housingTypes", Arrays.asList("Apartment", "House", "Condo", "Penthouse"));
        mav.addObject("housing", housing);
        mav.addObject("detailImages", new DetailImage());
        mav.addObject("accountId", id);
        return mav;
    }

    @PostMapping("/admin/{ownerId}/saveHousing")
    public String saveHousing(
            @ModelAttribute Housing housing,
            @PathVariable Long ownerId,
            @RequestParam("image") MultipartFile mainImage,
            @RequestParam("moreImages") MultipartFile[] moreImages) throws IOException {

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
        Housing savedHousing = housingRepo.save(housing);

        System.out.println(moreImages.length);
        if (moreImages.length > 1) {
            for (MultipartFile image: moreImages) {
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
        return "redirect:/admin/{ownerId}/myHousings";
    }

    @GetMapping("/admin/{accountId}/housings")
    public ModelAndView showAllHousings(@PathVariable Long accountId) {
        ModelAndView mav = new ModelAndView("admin-all-housings");
        List<Housing> housings = housingRepo.findAll();
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

    @PostMapping("/admin/{accountId}/searchHousing")
    public ModelAndView searchHousing(
            @PathVariable Long accountId,
            @ModelAttribute SearchRequest searchRequest
    ) {
        List<Housing> housings = housingDAO.findHousingsWithCriteria(searchRequest);
        ModelAndView mav = showSearchedHousings(housings);
        return mav;
    }

    public ModelAndView showSearchedHousings(List<Housing> housings) {
        ModelAndView mav = new ModelAndView("admin-all-housings");
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

    @GetMapping("/admin/{adminAccountId}/myHousings")
    public ModelAndView adminHousings(@PathVariable Long adminAccountId) {
        ModelAndView mav = new ModelAndView("admin-housings");
        Account account = accRepo.findById(adminAccountId).get();
        List<Housing> ownHousings = housingRepo.findByOwnerId(adminAccountId);
        mav.addObject("account", account);
        mav.addObject("ownHousings", ownHousings);
        return mav;
    }

    /*@GetMapping("/{housingId}/moreImages")
    public ModelAndView moreImages(@PathVariable Long housingId) {
        ModelAndView mav = new ModelAndView("more-images");
        Housing housing = housingRepo.findById(housingId).get();
        List<DetailImage> detailImages = detailImageRepository.findByHousingId(housingId);
        mav.addObject("detailImages", detailImages);
        mav.addObject("mainImageUrl", housing.getImageUrl());
        return mav;
    }*/

    @GetMapping("/admin/{accountId}/editHousing/{housingId}")
    public ModelAndView editHousing(@PathVariable Long housingId) {
        ModelAndView mav = new ModelAndView("admin-edit-housing");
        Housing housing = housingRepo.findById(housingId).get();
        mav.addObject("housing", housing);
        mav.addObject("housingTypes", getHousingTypes());
        return mav;
    }

    @GetMapping("/admin/{accountId}/deleteHousing/{housingId}")
    public String deleteHousing(@PathVariable Long housingId) {
        housingRepo.deleteById(housingId);
        return "redirect:/admin/{accountId}/myHousings";
    }

    /*@GetMapping("/{housingId}/images")
    public ModelAndView showImages(@PathVariable Long housingId) {
        ModelAndView mav = new ModelAndView("images");
        List<DetailImage> images = detailImageRepository.findByHousingId(housingId);
        System.out.println(images.size());
        Housing housing = housingRepo.findById(housingId).get();
        mav.addObject("images", images);
        mav.addObject("housing", housing);
        return mav;
    }*/

    /*@GetMapping("/{housingId}/{imageId}/delete")
    public String deleteImage(
            @PathVariable Long imageId,
            @PathVariable Long housingId) {
        detailImageRepository.deleteById(imageId);
        return "redirect:/{housingId}/images";
    }*/

    @GetMapping("/admin/{accountId}/fav/{housingId}")
    public String favHousing(
            @PathVariable Long accountId,
            @PathVariable Long housingId
    ) {
        List<WishList> wishLists = wishListRepository.findByAccountId(accountId);
        for (WishList wishList: wishLists) {
            if (wishList.getHousingId().equals(housingId)) {
                return "redirect:/admin/{accountId}/housings";
            }
        }

        WishList wishList = new WishList(accountId, housingId);
        wishListRepository.save(wishList);
        return "redirect:/admin/{accountId}/housings";
    }

    @GetMapping("/admin/{accountId}/favourites")
    public ModelAndView favouriteHousings(@PathVariable Long accountId) {
        List<Housing> housings = new ArrayList<>();
        List<WishList> wishLists = wishListRepository.findByAccountId(accountId);
        for (WishList wishList: wishLists) {
            Housing housing = housingRepo.findById(wishList.getHousingId()).get();
            housings.add(housing);
        }
        return showSearchedHousings(housings);
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }

    private List<String> getHousingTypes() {
        return Arrays.asList("Apartment", "House", "Condo", "Penthouse");
    }
}
