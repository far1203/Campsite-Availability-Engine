document.getElementById('trackerForm').addEventListener('submit', async function(event) {

    event.preventDefault();

    const email = document.getElementById('email').value.trim();
    const campgroundName = document.getElementById('campgroundName').value.trim();
    const state = document.getElementById('state').value;
    const campsiteNumber = document.getElementById('campsiteNumber').value.trim();
    const targetDate = document.getElementById('targetDate').value;

    const messageDisplay = document.getElementById('messageDisplay');

    if (!email) {
        messageDisplay.textContent = "Email is required.";
        messageDisplay.style.color = '#FAF0E6'; 
        messageDisplay.style.textShadow = '0 3px 8px rgba(255, 3, 3)';
        return;
    }
    
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        messageDisplay.textContent = "Please enter a valid email address.";
        messageDisplay.style.color = '#FAF0E6'; 
        messageDisplay.style.textShadow = '0 3px 8px rgba(255, 3, 3)';
        return;
    }
    
    if (!campgroundName) {
        messageDisplay.textContent = "Campground name is required.";
        messageDisplay.style.color = '#FAF0E6'; 
        messageDisplay.style.textShadow = '0 3px 8px rgba(255, 3, 3)';
        return;
    }
    
    if (!state) {
        messageDisplay.textContent = "Please select a state.";
        messageDisplay.style.color = '#FAF0E6'; 
        messageDisplay.style.textShadow = '0 3px 8px rgba(255, 3, 3)';
        return;
    }
    
    if (!campsiteNumber) {
        messageDisplay.textContent = "Campsite number is required.";
        messageDisplay.style.color = '#FAF0E6'; 
        messageDisplay.style.textShadow = '0 3px 8px rgba(255, 3, 3)';
        return;
    }
    
    if (!targetDate) {
        messageDisplay.textContent = "Date is required.";
        messageDisplay.style.color = '#FAF0E6'; 
        messageDisplay.style.textShadow = '0 3px 8px rgba(255, 3, 3)';
        return;
    }
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const selectedDate = new Date(targetDate);
    
    if (selectedDate < today) {
        messageDisplay.textContent = "Date cannot be in the past.";
        messageDisplay.style.color = '#FAF0E6'; 
        messageDisplay.style.textShadow = '0 3px 8px rgba(255, 3, 3)';
        return;
    }

    const payloadData = {
        email,
        campgroundName,
        state: state.toUpperCase(),
        campsiteNumber,
        targetDate
    };

    console.log('Sending this JSON payload data to Java:', payloadData);


    try {
        const response = await fetch('https://campsite-availability-engine.onrender.com/api/v1/subscriptions/track', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify(payloadData) 
        });

        const responseMessageText = await response.text();

    


        if (response.ok) {
            
            messageDisplay.textContent = responseMessageText;
            messageDisplay.style.color = '#FAF0E6'; 
            messageDisplay.style.textShadow = '0 3px 8px rgba(18, 247, 2)';

            document.getElementById('trackerForm').reset(); 
        } else {
            
            messageDisplay.textContent = responseMessageText;
            messageDisplay.style.color = '#FAF0E6'; 
            messageDisplay.style.textShadow = '0 3px 8px rgba(255, 3, 3)';
        }

    } catch (networkError) {
        console.error('Network failure:', networkError);
        alert('Could not connect to backend server. Make sure Spring Boot is running on port 8080.');
    }




})