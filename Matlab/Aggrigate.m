function Aggrigate
data(1,:,:) = beacon10_fe_ed_b2_22_7c();
data(2,:,:) = beacon10_fe_ed_d1_aa_47();
data(3,:,:) = beaconf8_1a_67_ee_fd_ee();
hold on
for i = 1:size(data,1)
    figure(1);
    [m, avg] = CollectData(data(i, :, :));
    plot(1:m, DistanceModel(4, 20, avg, 2442));
end
%fspl = 0-20*log10(1:size(data,2))-20*log10(2400)+27.55;
%plot(0:size(data,2)-1, fspl)
plot(1:m, 1:m)

title('Distance Model')
xlabel('meters')
ylabel('Est meters')

end