package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("itemForm", new BookForm());
        return "items/createItemForm";
    }


    @PostMapping("/items/new")
    public String create(@Valid @ModelAttribute("itemForm") BookForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "items/createItemForm";
        }

        Book book = Book.createBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId); // TYPE 에 따라 나누면 될듯 향후에는

        ModelMapper modelMapper = new ModelMapper();

        BookForm form = modelMapper.map(item, BookForm.class);

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form, @PathVariable("itemId") Long itemId) {

        // update 를 할 때 공격자가 id 값을 변경해서 악의적인 공격을 할 수 있다.
        // 이 유저가 이 ITEM(변경하려는 것)에 대한 권한이 있는지 체크를 해줘야 한다.
        // 이것도 그렇게 좋은 방식이 아니다 그럼 어떻게 하는 것이 제일 좋으냐?
//        ModelMapper modelMapper = new ModelMapper();
//
//        Book book = modelMapper.map(form, Book.class); // 준영속상태
//        itemService.saveItem(book);

        // 만약 수정을 해야 되는 어트리뷰트가 많다면 그냥 Dto 객체 하나 만들어서
        // 그거를 사용해서 하는 것이 제일 좋은 설계다
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }
}
