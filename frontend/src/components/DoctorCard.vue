<template>
  <div class="doctor-card">
    <div class="list-index" v-if="index">{{ index }}</div>
    <div class="doctor-info">
      <img :src="doctor.avatarUrl || 'https://i.imgur.com/gQkFvZU.png'" alt="Doctor Avatar" class="avatar" />
      <div class="details">
        <h3 class="name">{{ doctor.name }}</h3>
        <p class="department">{{ doctor.departmentName }}</p>
        <div class="rating">
          <span class="stars">{{ starRating }}</span>
          <span class="avg-rating">{{ doctor.avgRating ? doctor.avgRating.toFixed(1) : '暂无评分' }}</span>
        </div>
        <p class="address">123 Main St, Anytown, USA</p> <!-- Placeholder address -->
        <div class="tags">
          <span class="tag">Accepting new patients</span> <!-- Placeholder tag -->
        </div>
      </div>
    </div>
    <div class="schedule">
      <div v-if="isLoading" class="loading-schedule">Loading schedule...</div>
      <div v-else-if="scheduleDays.length === 0" class="no-schedule">No available appointments.</div>
      <template v-else>
        <div class="schedule-header">
          <span v-for="day in scheduleDays" :key="day.date">{{ day.label }}</span>
        </div>
        <div class="schedule-columns">
          <div class="time-slots" v-for="day in scheduleDays" :key="day.date">
            <button 
              v-for="slot in day.slots" 
              :key="slot.slotId"
              :class="['time-slot', { 'available': slot.available }]"
              :disabled="!slot.available"
            >
              {{ slot.startTime }}
            </button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { getAvailableDates, getTimeSlots } from '@/api/schedule';
import dayjs from 'dayjs';

const props = defineProps({
  doctor: {
    type: Object,
    required: true,
  },
  index: {
    type: Number,
    required: false,
  }
});

const isLoading = ref(true);
const scheduleDays = ref([]);

const fetchSchedule = async () => {
  try {
    isLoading.value = true;
    const availableDates = await getAvailableDates(props.doctor.id, 7);
    
    // We only want to display a max of 2 days
    const datesToFetch = availableDates.slice(0, 2);
    
    const schedulePromises = datesToFetch.map(async (dateInfo) => {
      const slots = await getTimeSlots(props.doctor.id, dateInfo.date);
      const dateLabel = dayjs(dateInfo.date).isSame(dayjs(), 'day') ? 'Today' : dayjs(dateInfo.date).format('MMM D');
      return {
        date: dateInfo.date,
        label: dateLabel,
        slots: slots.map(slot => ({
            ...slot,
            startTime: dayjs(slot.startTime).format('h:mm A')
        }))
      };
    });
    
    scheduleDays.value = await Promise.all(schedulePromises);

  } catch (error) {
    console.error(`Failed to fetch schedule for doctor ${props.doctor.id}:`, error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchSchedule();
});

const starRating = computed(() => {
  const rating = props.doctor.avgRating;
  if (!rating || rating === 0) {
    return '☆☆☆☆☆';
  }
  const fullStars = Math.floor(rating);
  const halfStar = rating % 1 >= 0.5 ? 1 : 0;
  const emptyStars = 5 - fullStars - halfStar;
  return '★'.repeat(fullStars) + (halfStar ? '½' : '') + '☆'.repeat(emptyStars);
});

</script>

<style scoped lang="scss">
.doctor-card {
  display: flex;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  background-color: #fff;
  transition: box-shadow 0.2s;
  position: relative;

  &:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  }
}

.list-index {
  position: absolute;
  top: -10px;
  left: -10px;
  width: 24px;
  height: 24px;
  background-color: #000;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  border: 2px solid #fff;
  z-index: 1;
}

.doctor-info {
  display: flex;
  gap: 1.5rem;
  width: 55%;
  border-right: 1px solid #e0e0e0;
  padding-right: 1.5rem;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.details {
  .name {
    font-size: 1.25rem;
    font-weight: bold;
    margin: 0 0 0.25rem;
  }
  .department {
    font-size: 1rem;
    color: #555;
    margin: 0 0 0.5rem;
  }
  .rating {
    display: flex;
    align-items: center;
    margin-bottom: 0.5rem;
    .stars {
      color: #ffd300;
    }
    .avg-rating {
      margin-left: 0.5rem;
      font-weight: bold;
    }
  }
  .address {
    font-size: 0.9rem;
    color: #777;
    margin: 0 0 0.75rem;
  }
  .tags .tag {
    background-color: #e8f5e9;
    color: #4caf50;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-size: 0.8rem;
  }
}

.schedule {
  width: 45%;
  padding-left: 1.5rem;
}

.loading-schedule, .no-schedule {
  color: #888;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.schedule-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  text-align: center;
  margin-bottom: 1rem;
  font-weight: bold;
}

.schedule-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.time-slots {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 0.5rem;
}

.time-slot {
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #ccc;
  background-color: #f0f0f0;
  color: #aaa;
  text-align: center;
  cursor: not-allowed;
  font-size: 0.9rem;
}

.time-slot.available {
  background-color: #ffd300;
  border-color: #f2c600;
  color: #000;
  cursor: pointer;
  font-weight: bold;

  &:hover {
    background-color: #f2c600;
  }
}
</style>
