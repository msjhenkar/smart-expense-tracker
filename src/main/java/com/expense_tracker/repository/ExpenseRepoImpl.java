package com.expense_tracker.repository;

import com.expense_tracker.model.Expense;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ExpenseRepoImpl implements ExpenseRepository{

    private final Map<Long, Expense> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Expense save(Expense expense) {
        if(expense.getId() == null){
            expense.setId(idGenerator.incrementAndGet());
        }
        store.put(expense.getId(),expense);

        return expense;
    }

    @Override
    public List<Expense> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<Expense> findByCategory(String category) {
        return store.values().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Expense> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
