package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1L;

    @Override
    public Item save(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() != null && item.getOwner().getId().equals(ownerId)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<Item> search(String text) {
        String lowerText = text.toLowerCase();
        List<Item> result = new ArrayList<>();

        for (Item item : items.values()) {
            boolean matchesName = item.getName() != null &&
                    item.getName().toLowerCase().contains(lowerText);
            boolean matchesDescription = item.getDescription() != null &&
                    item.getDescription().toLowerCase().contains(lowerText);

            if ((matchesName || matchesDescription) &&
                    item.getAvailable() != null && item.getAvailable()) {
                result.add(item);
            }
        }

        return result;
    }
}
