package com.bullish.checkout.domain.dealapplicator;

/*
    There are many ways in which deal(s) can be applied on a given basket.
    For example, if we have multiple regions, we'd probably want to have region-specific deal
    applicators that work based on the region's marketing and regulatory requirements.
    We might also want to apply deals based on the user profile, we may choose to have different
    floors and ceilings based on different profile.

    While I can't bake in those assumptions here, this interface and its implementations will allow
    us to customize the rules that govern how deals are applied given the particular context. We can also
    use this to design the stages of applying a deal by having - prepare, commit, side-effects etc
 */
public interface DealApplicator {

    BasketCheckout calculate();

//    BasketCheckout commit();
}
