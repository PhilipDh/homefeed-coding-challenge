package de.breuninger.coding.challenge.homefeed.testdata;

import de.breuninger.coding.challenge.homefeed.repository.BannerRepository;
import de.breuninger.coding.challenge.homefeed.repository.HighlightRepository;
import de.breuninger.coding.challenge.homefeed.repository.UserRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.BannerEntity;
import de.breuninger.coding.challenge.homefeed.repository.entity.HighlightEntity;
import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TestDataInitializer {
    private final UserRepository userRepository;
    private final BannerRepository bannerRepository;
    private final HighlightRepository highlightRepository;

    public TestDataInitializer(UserRepository userRepository,
                                BannerRepository bannerRepository,
                                HighlightRepository highlightRepository) {
        this.userRepository = userRepository;
        this.bannerRepository = bannerRepository;
        this.highlightRepository = highlightRepository;
    }

    public void initializeAllTestData() {
        createTestUsers();
        createTestBanners();
        createTestHighlights();
    }

    public void createTestUsers() {
        UserEntity vipUser = new UserEntity();
        vipUser.setUserId("user123");
        vipUser.setFirstname("Anna");
        vipUser.setSurname("Schmidt");
        vipUser.setPreferredCategories(Set.of("FASHION", "ACCESSORIES"));
        vipUser.setUserSegment("VIP");
        vipUser.setTotalOrders(15);
        userRepository.save(vipUser);

        UserEntity returningUser = new UserEntity();
        returningUser.setUserId("user456");
        returningUser.setFirstname("Max");
        returningUser.setSurname("Müller");
        returningUser.setPreferredCategories(Set.of("ELECTRONICS", "SPORTS"));
        returningUser.setUserSegment("RETURNING");
        returningUser.setTotalOrders(5);
        userRepository.save(returningUser);

        UserEntity newUser = new UserEntity();
        newUser.setUserId("user789");
        newUser.setFirstname("Lisa");
        newUser.setSurname("Weber");
        newUser.setPreferredCategories(Set.of("HOME", "BEAUTY"));
        newUser.setUserSegment("NEW");
        newUser.setTotalOrders(0);
        userRepository.save(newUser);
    }

    public void createTestBanners() {
        LocalDateTime now = LocalDateTime.now();

        BannerEntity generalPromo = new BannerEntity();
        generalPromo.setTitle("Spring Sale");
        generalPromo.setMessage("Up to 30% off on selected items!");
        generalPromo.setBannerType("PROMO");
        generalPromo.setImageUrl("https://example.com/banners/spring-sale.jpg");
        generalPromo.setActionUrl("/products/sale");
        generalPromo.setActionLabel("Shop Now");
        generalPromo.setPriority(10);
        generalPromo.setValidFrom(now.minusDays(5));
        generalPromo.setValidUntil(now.plusDays(25));
        generalPromo.setTargetUserSegments(List.of("ALL"));
        bannerRepository.save(generalPromo);

        BannerEntity vipPromo = new BannerEntity();
        vipPromo.setTitle("VIP Exclusive: Early Access");
        vipPromo.setMessage("Get early access to our new collection!");
        vipPromo.setBannerType("PROMO");
        vipPromo.setImageUrl("https://example.com/banners/vip-exclusive.jpg");
        vipPromo.setActionUrl("/products/new-collection");
        vipPromo.setActionLabel("Explore Now");
        vipPromo.setPriority(15);
        vipPromo.setValidFrom(now.minusDays(3));
        vipPromo.setValidUntil(now.plusDays(20));
        vipPromo.setTargetUserSegments(List.of("VIP"));
        bannerRepository.save(vipPromo);

        BannerEntity infoBanner = new BannerEntity();
        infoBanner.setTitle("New Features Available");
        infoBanner.setMessage("Check out our improved search and filtering!");
        infoBanner.setBannerType("INFO");
        infoBanner.setIconUrl("https://example.com/icons/info.png");
        infoBanner.setActionUrl("/features");
        infoBanner.setActionLabel("Learn More");
        infoBanner.setPriority(5);
        infoBanner.setValidFrom(now.minusDays(2));
        infoBanner.setValidUntil(now.plusDays(30));
        infoBanner.setTargetUserSegments(List.of("ALL"));
        bannerRepository.save(infoBanner);
    }

    public void createTestHighlights() {
        LocalDateTime now = LocalDateTime.now();

        HighlightEntity fashionHighlight = new HighlightEntity(
                null,
                "Spring Collection 2026",
                "Discover the freshest styles for the new season.",
                "https://cdn.example.com/highlights/spring-2026.jpg",
                List.of("FASHION", "ACCESSORIES"),
                now.minusDays(10),
                now.plusDays(50)
        );
        highlightRepository.save(fashionHighlight);

        HighlightEntity techHighlight = new HighlightEntity(
                null,
                "Latest Tech Arrivals",
                "New smartphones, smartwatches, and wireless earbuds.",
                "https://cdn.example.com/highlights/tech-2026.jpg",
                List.of("ELECTRONICS", "ACCESSORIES"),
                now.minusDays(5),
                now.plusDays(30)
        );
        highlightRepository.save(techHighlight);

        HighlightEntity homeHighlight = new HighlightEntity(
                null,
                "Cozy Home Essentials",
                "Transform your space with our curated collection.",
                "https://cdn.example.com/highlights/home-essentials.jpg",
                List.of("HOME", "FURNITURE"),
                now.minusDays(15),
                now.plusDays(45)
        );
        highlightRepository.save(homeHighlight);

        HighlightEntity sportsHighlight = new HighlightEntity(
                null,
                "Get Active - Spring Fitness",
                "Gear up for outdoor adventures.",
                "https://cdn.example.com/highlights/fitness-spring.jpg",
                List.of("SPORTS", "FITNESS", "OUTDOOR"),
                now.minusDays(7),
                now.plusDays(60)
        );
        highlightRepository.save(sportsHighlight);

        HighlightEntity beautyHighlight = new HighlightEntity(
                null,
                "Beauty Refresh",
                "New skincare routines and makeup trends.",
                "https://cdn.example.com/highlights/beauty-refresh.jpg",
                List.of("BEAUTY", "SKINCARE", "WELLNESS"),
                now.minusDays(3),
                now.plusDays(40)
        );
        highlightRepository.save(beautyHighlight);
    }
}

