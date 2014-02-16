function FindPattern(data)

average_data=zeros(length(data),1,1);
for i = 1:length(data)
    average_data(i) = data(i, 1) + data(i,2) + data(i,3) +data(i, 4) + data(i, 5);
    average_data(i) = average_data(i) / 5;
end
figure(1);

plot(0:length(average_data)-1,average_data);
title('average')
xlabel('meters')
ylabel('dbm')
distance  = 1:(length(data)-1);
fspl = 0-20*log10(distance);%+20*log10(2447)-27.55;
obs = average_data(2:length(average_data))-average_data(1);

figure(2);

plot(distance, fspl, distance, obs);
title('losses vs expected')
xlabel('meters')
ylabel('delta dbm')
end