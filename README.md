# Smart Medication Monitoring System

## Overview

The Smart Medication Monitoring System is an IoT-based Android application designed to monitor and manage medication intake. It integrates with ThingSpeak cloud services to fetch real-time data from various sensors, such as temperature sensors, heart rate monitors, and medication box sensors. The application displays this data and triggers notifications if any values fall outside predefined thresholds, ensuring timely alerts for critical conditions.

## Features

- **Real-time Data Fetching**: Continuously retrieves sensor data from ThingSpeak every 5 seconds.
- **Temperature Monitoring**: Displays current temperature and alerts if the temperature is too high.
- **Heart Rate Monitoring**: Displays current heart rate and alerts if it’s too high or too low.
- **Medication Box Monitoring**: Displays the status of the medication box and alerts if it’s empty.
- **Emergency Alerts**: Provides emergency notifications based on sensor data.
- **Pills Status**: Shows whether the patient has taken their medication or not.

## Getting Started

### Prerequisites

- Android Studio
- Internet access for fetching data from ThingSpeak
- ThingSpeak API Key and Channel ID

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/smart-medication-monitoring-system.git
   cd smart-medication-monitoring-system
