package com.chaoqunhuang;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

import java.util.Map;

/**
 *
 * Created by chaoqunhuang on 6/17/18.
 */
public class LambdaHandler implements RequestHandler<Object, String> {
    private final String ARN = "arn:aws:sns:us-east-1:842132808572:Amazon_New_Grad";
    private final String PHONE_REG = "^[0-9]{11}$";
    private final String SUBS_SUCCESS = "Thanks for subscribing MXB AMZ_NEW_GRAD! You will get notification if there is " +
            "new opening for Amazon New Grad based in Seattle";

    public String handleRequest(Object placeHolder, Context context) {
        Map<String, String> map = (Map) placeHolder;
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        if ("little sheep".equals(map.get("Validation"))) {
            if ((map.get("Phone")).matches(PHONE_REG)) {
                SubscribeRequest subRequest = new SubscribeRequest(ARN, "sms", map.get("Phone"));
                snsClient.subscribe(subRequest);

                //get request id for SubscribeRequest from SNS metadata
                System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));

                PublishResult result = snsClient.publish(new PublishRequest()
                        .withMessage(SUBS_SUCCESS)
                        .withPhoneNumber(map.get("Phone")));
                System.out.println(result); // Prints the message ID.
                return "SUCCESS";
            } else {
                return "The Phone Number is not in a valid format";
            }
        } else {
            System.out.println("The answer is wrong!");
            return "The Validation is incorrect!";
        }
    }
}
