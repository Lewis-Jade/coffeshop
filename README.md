# ☕ MMUST Coffee Shop

A modern mobile application for ordering coffee at Masinde Muliro University of Science and Technology.

## 🎯 Project Overview

Students and staff at MMUST face long queues when purchasing coffee. This app solves that by enabling digital ordering and M-PESA payments, reducing wait times and modernizing the campus coffee shop experience.

---

## ✅ Features Completed

### User Features
- ✅ **User Authentication** - Register and login with email/phone
- ✅ **Browse Products** - View available coffee items with prices and images
- ✅ **Shopping Cart** - Add, update, and remove items
- ✅ **M-PESA Payments** - Secure mobile money payments
- ✅ **Order Tracking** - View order history and status
- ✅ **User Profile** - Manage account settings

### Technical Features
- ✅ **Supabase Backend** - Authentication and database
- ✅ **Secure Storage** - No hardcoded credentials
- ✅ **Responsive UI** - Works on all Android devices
- ✅ **Error Handling** - Graceful error messages
- ✅ **Offline Cart** - Cart persists locally

---

## 🚧 Work in Progress

### Planned Features
- ⏳ **Admin Dashboard** - Manage products and orders
- ⏳ **Push Notifications** - Real-time order updates
- ⏳ **Product Search** - Find items quickly
- ⏳ **Order History Details** - View individual order items
- ⏳ **Password Reset** - Forgot password functionality

### Future Enhancements
- 📋 **Multiple Coffee Shops** - Support for different campus locations
- 📋 **Loyalty Program** - Reward frequent customers
- 📋 **Product Reviews** - Rate and review items
- 📋 **Favorites** - Save preferred items

---

## 🛠️ Tech Stack

- **Platform:** Native Android (Java)
- **UI:** Material Design 3
- **Backend:** Supabase (PostgreSQL, Auth)
- **Payments:** Paystack (M-PESA integration)
- **Local Storage:** SharedPreferences, Room Database
- **Networking:** OkHttp
- **Build:** Gradle

---

## 🚀 Quick Start

### Prerequisites
- Android Studio
- Android device or emulator (API 24+)
- Supabase account
- Paystack account (test mode)

### Setup
```bash
# Clone the repository
git clone https://github.com/sibby-killer/coffeshop.git
cd coffeshop

# Copy credentials template
cp local.properties.template local.properties

# Add your credentials to local.properties
# - Get Supabase URL and key from: https://supabase.com/dashboard
# - Get Paystack test key from: https://dashboard.paystack.com

# Open in Android Studio
# File → Open → Select coffeshop folder

# Sync and build
# File → Sync Project with Gradle Files
# Build → Rebuild Project

# Run
# Click Run ▶️
```

For detailed setup instructions, see [documentation/quick-start.md](documentation/quick-start.md)

---

## 📱 Screenshots

<table>
  <tr>
    <td>Login</td>
    <td>Products</td>
    <td>Cart</td>
    <td>Checkout</td>
  </tr>
  <tr>
    <td><i>Authentication</i></td>
    <td><i>Browse Menu</i></td>
    <td><i>Manage Cart</i></td>
    <td><i>M-PESA Payment</i></td>
  </tr>
</table>

---

## 🤝 Contributing

### Getting Started
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Set up your own `local.properties` with credentials
4. Make your changes
5. Test thoroughly
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

### Code Style
- Follow existing Java conventions
- Use Material Design components
- Keep UI consistent with current design
- Add comments for complex logic
- Write meaningful commit messages

---

## 📚 Documentation

- [Quick Start Guide](documentation/quick-start.md) - Get up and running
- [Database Setup](documentation/database-setup.md) - Configure Supabase
- [Troubleshooting](documentation/troubleshooting.md) - Fix common issues
- [Deployment](documentation/deployment.md) - Production deployment

---

## 🔐 Security

- **No hardcoded credentials** - All secrets in `local.properties` (gitignored)
- **Row Level Security** - Database access controlled by Supabase RLS
- **HTTPS only** - All API communication encrypted
- **Secure payments** - PCI-compliant via Paystack
- **Input validation** - All user inputs validated

---

## 📊 Project Status

- **Version:** 1.0
- **Status:** Active Development
- **Last Updated:** January 2026
- **Build Status:** ✅ Passing
- **Test Coverage:** Manual testing complete

---

## 📝 License

This project is for educational purposes at MMUST.

---

## 👥 Team

Developed by students at Masinde Muliro University of Science and Technology.

---

## 🐛 Known Issues

- Payment verification requires returning to app after browser payment
- Product images are currently local drawables (not from database)

See [documentation/troubleshooting.md](documentation/troubleshooting.md) for solutions.

---

## 📞 Support

Having issues? Check these in order:
1. [Troubleshooting Guide](documentation/troubleshooting.md)
2. [Quick Start Guide](documentation/quick-start.md)
3. Open an issue on GitHub

---

**Happy Coding!** ☕
