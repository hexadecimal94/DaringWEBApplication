package jp.co.poweredge.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import jp.co.poweredge.store.domain.Order;
import jp.co.poweredge.store.domain.User;

public interface OrderRepository extends CrudRepository<Order, Long> {

	//ユーザのすべての注文をリストに入れる
	List<Order> findByUser(User user);

	@EntityGraph(attributePaths = { "cartItems", "payment", "shipping" })
	Order findEagerById(Long id);

	//Below query will be executed when this EntityGraph is called method is called
	/*sql query

	 select
        ordertable.id as orderid,

        shippingtable.id as shippingid_from_shippingtable,
        paymenttable.id as paymentid_from_paymenttable,
        cartitemtable.id as cartitemid_from_cartitemtable,

        ordertable.order_date as orderdate,
        ordertable.order_status as orderstatus,
        ordertable.order_total as ordertotal,
        ordertable.payment_id as order_payment_id,
        ordertable.shipping_id as order_shippingid,
        ordertable.shipping_date as order_shippingdate,
        ordertable.user_id as order_userid,

        shippingtable.address_id as shipping_address,
        shippingtable.order_id as shipping_order_id,
        shippingtable.receiver as shipping_receiver,

        paymenttable.card_name as payment_card_name,
        paymenttable.card_number as payment_card_number,
        paymenttable.cvc as payment_cvc,
        paymenttable.expiry_month as payment_expiry_month,
        paymenttable.expiry_year as payment_expiry_year,
        paymenttable.holder_name as payemnt_holder_name,
        paymenttable.order_id as payment_order_id,
        paymenttable.type as payment_type,

        cartitemtable.article_id as cart_item_article_id,
        cartitemtable.order_id as cart_item_order_id,
        cartitemtable.qty as cart_item_quantity,
        cartitemtable.size as cart_item_size,
        cartitemtable.user_id as cart_item_user_id,
        cartitemtable.order_id as cart_item_order_id,
        cartitemtable.id as cart_itemid
    from
        user_order ordertable
    left outer join
        shipping shippingtable
            on ordertable.shipping_id=shippingtable.id
    left outer join
        payment paymenttable
            on ordertable.payment_id=paymenttable.id
    left outer join
        cart_item cartitemtable
            on ordertable.id=cartitemtable.order_id
    where
        ordertable.id=411;
*/
//	public void updateOrderRecord(Long id, String h);

	@Query(value="SELECT (*)" +
			"FROM user_order u" +
			"WHERE MONTH(u.order_date) = :month;", nativeQuery=true)
	List<Order> findOrderByOrderDateandMonth(@Param("month")String month);
}
