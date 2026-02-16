package de.breuninger.coding.challenge.homefeed.config;

import de.breuninger.coding.challenge.homefeed.repository.HighlightRepository;
import de.breuninger.coding.challenge.homefeed.repository.BannerRepository;
import de.breuninger.coding.challenge.homefeed.repository.UserRepository;
import de.breuninger.coding.challenge.homefeed.repository.entity.BannerEntity;
import de.breuninger.coding.challenge.homefeed.repository.entity.HighlightEntity;
import de.breuninger.coding.challenge.homefeed.repository.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
public class MockDataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               BannerRepository bannerRepository,
                               HighlightRepository highlightRepository) {
        return args -> {
            initUsers(userRepository);
            initBanners(bannerRepository);
            initHighlights(highlightRepository);
        };
    }


    private void initUsers(UserRepository userRepository) {
        UserEntity user1 = new UserEntity();
        user1.setUserId("user123");
        user1.setFirstname("Anna");
        user1.setSurname("Schmidt");
        user1.setPreferredCategories(Set.of("FASHION", "ACCESSORIES"));
        user1.setUserSegment("VIP");

        user1.setTotalOrders(15);
        userRepository.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setUserId("user456");
        user2.setFirstname("Max");
        user2.setSurname("Müller");
        user2.setPreferredCategories(Set.of("ELECTRONICS", "SPORTS"));
        user2.setUserSegment("RETURNING");
        user2.setTotalOrders(5);
        userRepository.save(user2);

        UserEntity user3 = new UserEntity();
        user3.setUserId("user789");
        user3.setFirstname("Lisa");
        user3.setSurname("Weber");
        user3.setPreferredCategories(Set.of("HOME", "BEAUTY"));
        user3.setUserSegment("NEW");
        user3.setTotalOrders(0);
        userRepository.save(user3);
    }
    
    private void initBanners(BannerRepository bannerRepository) {
        LocalDateTime now = LocalDateTime.now();

        BannerEntity promo1 = new BannerEntity();
        promo1.setTitle("Mid Season Sale");
        promo1.setMessage("Up to 30% off on selected items. Limited time offer!");
        promo1.setBannerType("PROMO");
        promo1.setImageUrl("https://example.com/banners/mid-season-sale.jpg");
        promo1.setActionUrl("/products/sale");
        promo1.setActionLabel("Shop Now");
        promo1.setPriority(10);
        promo1.setValidFrom(now.minusDays(5));
        promo1.setValidUntil(now.plusDays(25));
        promo1.setTargetUserSegments(List.of("ALL"));
        bannerRepository.save(promo1);

        // VIP Exclusive Promo
        BannerEntity promoVip = new BannerEntity();
        promoVip.setTitle("VIP Exclusive: Early Access");
        promoVip.setMessage("Get early access to our new collection. VIP members only!");
        promoVip.setBannerType("PROMO");
        promoVip.setImageUrl("https://example.com/banners/vip-exclusive.jpg");
        promoVip.setActionUrl("/products/new-collection");
        promoVip.setActionLabel("Explore Now");
        promoVip.setPriority(15);
        promoVip.setValidFrom(now.minusDays(3));
        promoVip.setValidUntil(now.plusDays(20));
        promoVip.setTargetUserSegments(List.of("VIP"));
        bannerRepository.save(promoVip);

        BannerEntity info1 = new BannerEntity();
        info1.setTitle("New Features Available");
        info1.setMessage("Check out our improved search and filtering options!");
        info1.setBannerType("INFO");
        info1.setIconUrl("https://example.com/icons/info.png");
        info1.setActionUrl("/features");
        info1.setActionLabel("Learn More");
        info1.setPriority(5);
        info1.setValidFrom(now.minusDays(2));
        info1.setValidUntil(now.plusDays(30));
        info1.setTargetUserSegments(List.of("ALL"));
        bannerRepository.save(info1);
    }

    private void initHighlights(HighlightRepository highlightRepository) {
        LocalDateTime now = LocalDateTime.now();

        HighlightEntity highlight1 = new HighlightEntity(
                null,
                "Spring Collection 2026",
                "Discover the freshest styles for the new season. Light fabrics, vibrant colors.",
                "https://cdn.example.com/highlights/spring-2026.jpg",
                List.of("FASHION", "ACCESSORIES"),
                now.minusDays(10),
                now.plusDays(50)
        );
        highlightRepository.save(highlight1);

        HighlightEntity highlight2 = new HighlightEntity(
                null,
                "Latest Tech Arrivals",
                "New smartphones, smartwatches, and wireless earbuds just dropped.",
                "https://cdn.example.com/highlights/tech-feb-2026.jpg",
                List.of("ELECTRONICS", "ACCESSORIES"),
                now.minusDays(5),
                now.plusDays(30)
        );
        highlightRepository.save(highlight2);

        HighlightEntity highlight3 = new HighlightEntity(
                null,
                "Cozy Home Essentials",
                "Transform your space with our curated home collection. Up to 40% off.",
                "https://cdn.example.com/highlights/home-essentials.jpg",
                List.of("HOME", "FURNITURE"),
                now.minusDays(15),
                now.plusDays(45)
        );
        highlightRepository.save(highlight3);

        HighlightEntity highlight4 = new HighlightEntity(
                null,
                "Get Active - Spring Fitness",
                "Gear up for outdoor adventures. Running shoes, yoga mats, and more.",
                "https://cdn.example.com/highlights/fitness-spring.jpg",
                List.of("SPORTS", "FITNESS", "OUTDOOR"),
                now.minusDays(7),
                now.plusDays(60)
        );
        highlightRepository.save(highlight4);

        HighlightEntity highlight5 = new HighlightEntity(
                null,
                "Beauty Refresh",
                "New skincare routines and makeup trends for spring.",
                "https://cdn.example.com/highlights/beauty-refresh.jpg",
                List.of("BEAUTY", "SKINCARE", "WELLNESS"),
                now.minusDays(3),
                now.plusDays(40)
        );
        highlightRepository.save(highlight5);

        HighlightEntity highlight6 = new HighlightEntity(
                null,
                "Accessorize Your Style",
                "Complete your look with bags, watches, and jewelry.",
                "https://cdn.example.com/highlights/accessories-spring.jpg",
                List.of("ACCESSORIES", "FASHION"),
                now.minusDays(12),
                now.plusDays(55)
        );
        highlightRepository.save(highlight6);

        HighlightEntity expiredHighlight = new HighlightEntity(
                null,
                "Winter Clearance",
                "Last chance winter sale - should not appear in feed.",
                "https://cdn.example.com/highlights/winter-clearance.jpg",
                List.of("FASHION", "HOME"),
                now.minusDays(90),
                now.minusDays(10)
        );
        highlightRepository.save(expiredHighlight);

        HighlightEntity futureHighlight = new HighlightEntity(
                null,
                "Summer Preview 2026",
                "Coming soon - beach essentials and swimwear.",
                "https://cdn.example.com/highlights/summer-preview.jpg",
                List.of("FASHION", "SWIMWEAR"),
                now.plusDays(30),
                now.plusDays(120)
        );
        highlightRepository.save(futureHighlight);

        HighlightEntity highlight7 = new HighlightEntity(
                null,
                "Gaming Setup Essentials",
                "Level up your gaming with new peripherals and accessories.",
                "https://cdn.example.com/highlights/gaming-setup.jpg",
                List.of("GAMING", "ELECTRONICS"),
                now.minusDays(8),
                now.plusDays(35)
        );
        highlightRepository.save(highlight7);

        HighlightEntity highlight8 = new HighlightEntity(
                null,
                "Valentine's Gift Guide",
                "Find the perfect gift - fashion, beauty, tech, and more.",
                "https://cdn.example.com/highlights/valentines-2026.jpg",
                List.of("FASHION", "BEAUTY", "ELECTRONICS", "ACCESSORIES"),
                now.minusDays(1),
                now.plusDays(10)
        );
        highlightRepository.save(highlight8);
    }

}
