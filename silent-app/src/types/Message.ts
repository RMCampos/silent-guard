export type Message = {
  id: number;
  title: string;
  content: string;
  daysToTrigger: number;
  recipient: string;
  active: boolean
};
