#include <robot>

int CppTest {

	string name;
	string speaker;
	int classification;
	
	cout << "Enter robot name: ";
	cin >> name;
	
	// Feature[ROBOT_SPEAKER=SPKR_A]
	cout << "Enter robot speaker a name: ";
	cin >> speaker;
	// Feature Else
	// cout << "Enter robot speaker b name: ";
	// cin >> speaker;
	// End Feature
	// ConfigurationGroup Not[abGroup]
	// cout << "Enter robot speaker a classification: ";
	// cin >> classification;
	// ConfigurationGroup Else
	cout << "Enter robot speaker b classification: ";
	cin >> classification;
	// End ConfigurationGroup
}
