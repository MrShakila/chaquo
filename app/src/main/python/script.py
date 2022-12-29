import pylsl

def create_stream():
    # Create the stream info
    stream_info = pylsl.StreamInfo('My Stream', 'EEG', 4, 100, pylsl.cf_float32, 'myuniqueid23443')
    # Create the outlet
    outlet = pylsl.StreamOutlet(stream_info)
    return outlet

# Test the function
outlet = create_stream()
print(outlet)
