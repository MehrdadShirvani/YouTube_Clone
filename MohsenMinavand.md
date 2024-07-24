# Project Features Presentation

## API

Our API system is designed using a **synchronous request-response model**. This design ensures that the client sends a request to the server and waits for the server to respond before continuing. This approach is straightforward and easy to understand, as each request is directly tied to a corresponding response.

### Request and Response Classes

We have distinct request and response classes that contain headers and bodies.

- **Headers**: Include endpoints and methods.
    - **Endpoints**: Define the specific resource or action the request targets.
    - **Methods**: Differentiate between the types of operations:
        - **GET**: Retrieve data.
        - **POST**: Submit new data.
        - **PUT**: Update existing data.
        - **DELETE**: Remove data.
- **Bodies**: Contain the necessary data for the request or response.

Every response includes:
- **Success**: Indicates whether the operation was successful.
- **Message**: Provides a response code and a brief message.

Before sending, all data is serialized using the **Jackson library** and encrypted.

## Encryption

We employ both **RSA (asymmetric)** and **AES (symmetric)** encryption methods.

- **AES (Advanced Encryption Standard)**:
    - Symmetric encryption means the same key is used for both encryption and decryption.
    - Fast and efficient for encrypting large amounts of data.
- **RSA (Rivest-Shamir-Adleman)**:
    - Asymmetric encryption uses a pair of keys (public and private).
    - Used primarily for secure key exchange.

In our system, AES is used to encrypt and decrypt data, while RSA is used to encrypt the AES key for secure transmission to the server. Each user is assigned a unique AES key, which is changed each time the app is opened, ensuring no one else can access it.

## Caching System

To enhance user experience, we cache client account data after the first login or sign-up. This cached account includes a checksum of the account object, allowing us to detect and prevent login if the cached file is tampered with. The cached account is deleted upon logout.

## Verification System

We have implemented an email verification system. Upon signing up, users must verify their email by entering a 6-digit code sent from our official email.

## Two-Factor Authentication (2FA)

After signing up and verifying their email, users can opt to set up 2FA for added security. This system works with authenticator apps like Google Authenticator. If any issues arise with the cached account, such as a password change or file tampering, users must verify their identity using either an authenticator app or an emailed code.

## Recommendation System

Our recommendation system rates videos based on four features:
1. **Trending Rate**: Calculated using view count, engagement, and growth, each weighted appropriately.
2. **Interesting Rate**: Based on the number of views per video in daily, weekly, monthly, and all-time periods, each with specific weights.
3. **Subscription Status**: Whether the user is subscribed to the channel.
4. **Publish Date**: Newer videos receive higher ratings.

These features are normalized to a range of 0 to 1 and summed to generate a total score for each video. Videos are then sorted by this score and recommended to users.

## Lazy Loading

**Lazy loading** is a technique that delays the loading of non-critical resources until they are needed. This approach improves app stability and memory management. We display 10 to 18 videos initially, and as the user scrolls to the bottom, more videos are loaded dynamically.

## Notification System

If you are subscribed to a channel and that channel uploads a new video, the server notifies the client. We push a notification using the system tray to alert the user about the new content.
