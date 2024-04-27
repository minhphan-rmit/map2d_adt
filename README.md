# Points of Interest (POI) Search

This project aims to develop an application similar to Google Maps with a focus on optimizing the search for Points of Interest (POI). Given a bounding rectangle and a type of service, the application returns a set of places providing that service within the specified area.

## Problem Statement

The primary goal of this project is to efficiently search for POIs within a given geographical area based on user-defined criteria such as service type and proximity. The application should be capable of handling a large number of places and service types while providing fast and accurate search results.

### Features

- Add, edit, and remove places on the map.
- Search for places based on service type and location.
- Limit the number of search results displayed.
- Support for various service types and geographic areas.

## Technical Description

### Map Size
- The map size is fixed at 10,000,000 x 10,000,000 units.

### Bounding Rectangle
- The bounding rectangle can range from 100 x 100 to 100,000 x 100,000 units.
- It defines the area within which the search for POIs is conducted.

### Service Types
- Up to 10 different service types are supported.
- Each place can offer multiple services.

### Number of Places
- The system can handle up to 100,000,000 places.

### Maximum Places Shown
- The maximum number of places shown in the search result is limited to 50.

### Euclidean Distance
- Euclidean distance is used for calculating the proximity of places.

### Map2D ADT
- An Abstract Data Type (ADT) called Map2D is implemented to support the required operations.
- Operations include Add, Edit, Remove, and Search.

## Usage

1. Clone the repository to your local machine.
2. Run the application and follow the on-screen instructions to interact with the map and perform searches.

## Testing

- Random data generation can be used to test the functionality of the application.
- Ensure that the application performs well under various scenarios and edge cases.

## Contributors

- [Your Name]
- [Your Email]
- [Your GitHub Profile]

## License

This project is licensed under the [License Name] License - see the [LICENSE](LICENSE) file for details.
